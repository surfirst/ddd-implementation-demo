workspace "电商系统 - DDD战略设计到战术设计" "展示限界上下文映射与容器架构" {

    model {
        # 外部系统和用户
        customer = person "客户" "在线购物的消费者" "External"
        admin = person "运营人员" "管理商品和订单" "Internal"
        
        paymentGateway = softwareSystem "第三方支付平台" "处理支付交易(微信/支付宝)" "External"
        logistics = softwareSystem "物流系统" "第三方物流服务商" "External"
        
        # 电商平台系统(包含多个限界上下文)
        ecommerce = softwareSystem "电商平台" "在线购物平台" {
            
            # ========== 1. 商品目录上下文 (Product Catalog Context) ==========
            # 战略定位: 支撑子域 - 提供商品浏览和搜索能力
            group "商品目录上下文 [支撑子域]" {
                catalogAPI = container "商品目录API" "提供商品查询、搜索、分类服务" "Spring Boot" "API"
                catalogDB = container "商品目录数据库" "存储商品信息、分类、SKU\n聚合根: Product, Category" "PostgreSQL" "Database"
                catalogSearch = container "商品搜索引擎" "提供全文搜索和推荐" "Elasticsearch" "SearchEngine"
            }
            
            # ========== 2. 订单上下文 (Order Context) ==========
            # 战略定位: 核心子域 - 订单生命周期管理
            group "订单上下文 [核心子域]" {
                orderAPI = container "订单API" "处理订单创建、修改、取消" "Spring Boot" "API"
                orderDomain = container "订单领域服务" "订单状态机、定价计算\n聚合根: Order, OrderLine" "Java Library" "DomainService"
                orderDB = container "订单数据库" "存储订单聚合、支付记录\nCQRS写模型" "PostgreSQL" "Database"
                orderReadDB = container "订单查询数据库" "订单列表、历史查询\nCQRS读模型" "MongoDB" "Database"
                orderMQ = container "订单消息队列" "发布订单领域事件" "RabbitMQ" "MessageBroker"
            }
            
            # ========== 3. 库存上下文 (Inventory Context) ==========
            # 战略定位: 核心子域 - 库存一致性保障
            group "库存上下文 [核心子域]" {
                inventoryAPI = container "库存API" "库存预占、扣减、释放" "Spring Boot" "API"
                inventoryDomain = container "库存领域服务" "库存并发控制、补货策略\n聚合根: InventoryItem" "Java Library" "DomainService"
                inventoryDB = container "库存数据库" "存储库存数量、预占记录" "PostgreSQL" "Database"
                inventoryCache = container "库存缓存" "高并发读写缓存" "Redis" "Cache"
            }
            
            # ========== 4. 支付上下文 (Payment Context) ==========
            # 战略定位: 支撑子域 - 支付流程编排
            group "支付上下文 [支撑子域]" {
                paymentAPI = container "支付API" "发起支付、查询支付状态" "Spring Boot" "API"
                paymentACL = container "支付防腐层" "适配多种支付网关\n实现: Adapter Pattern" "Java Library" "AntiCorruptionLayer"
                paymentDB = container "支付数据库" "存储支付流水、对账记录\n聚合根: Payment" "PostgreSQL" "Database"
            }
            
            # API网关 - 统一入口
            apiGateway = container "API网关" "路由、鉴权、限流" "Kong" "Gateway"
        }
        
        # ========== 关系定义 ==========
        
        # 用户交互
        customer -> apiGateway "浏览商品、下单、支付" "HTTPS"
        admin -> apiGateway "管理商品、订单" "HTTPS"
        
        # API网关路由
        apiGateway -> catalogAPI "路由商品请求"
        apiGateway -> orderAPI "路由订单请求"
        apiGateway -> inventoryAPI "路由库存请求"
        apiGateway -> paymentAPI "路由支付请求"
        
        # 商品目录上下文内部关系
        catalogAPI -> catalogDB "读写商品数据" "JDBC"
        catalogAPI -> catalogSearch "同步搜索索引" "HTTP"
        
        # 订单上下文内部关系
        orderAPI -> orderDomain "调用领域逻辑"
        orderDomain -> orderDB "持久化订单聚合" "JDBC"
        orderAPI -> orderReadDB "查询订单列表" "MongoDB Driver"
        orderDomain -> orderMQ "发布领域事件\n(OrderCreated, OrderPaid)" "AMQP"
        
        # 库存上下文内部关系
        inventoryAPI -> inventoryDomain "调用库存逻辑"
        inventoryDomain -> inventoryDB "更新库存" "JDBC"
        inventoryDomain -> inventoryCache "缓存库存数据" "Redis Protocol"
        
        # 支付上下文内部关系
        paymentAPI -> paymentACL "调用支付适配器"
        paymentACL -> paymentDB "记录支付流水" "JDBC"
        paymentACL -> paymentGateway "调用第三方支付\n(Customer-Supplier)" "HTTPS/Webhook"
        
        # ========== 上下文映射关系(跨边界集成) ==========
        # 注意: 每对上下文只有一种映射关系
        
        # 订单 -> 商品目录 (Customer-Supplier)
        # 订单是客户,依赖商品信息验证;商品是供应商,提供查询API
        orderAPI -> catalogAPI "验证商品信息\n(Customer-Supplier)" "REST API" "Sync"
        
        # 订单 -> 库存 (Customer-Supplier)  
        # 订单是客户,需要库存能力;库存是供应商,提供预占/扣减/释放API
        # 通过同步调用和异步事件组合实现最终一致性
        orderAPI -> inventoryAPI "同步预占库存\n(Customer-Supplier)" "REST API" "Sync"
        orderMQ -> inventoryAPI "异步释放库存\n(同一映射关系)" "AMQP" "Async"
        
        # 订单 -> 支付 (Customer-Supplier)
        # 订单发起支付请求,支付执行支付逻辑
        orderAPI -> paymentAPI "创建支付\n(Customer-Supplier)" "REST API" "Sync"
        paymentAPI -> orderMQ "支付结果回调\n(同一映射关系)" "AMQP" "Async"
        
        # 库存 -> 商品目录 (Conformist)
        # 库存完全遵从商品定义的SKU模型,无话语权
        inventoryAPI -> catalogAPI "同步SKU信息\n(Conformist)" "REST API" "Sync"
        
        # 支付防腐层 -> 外部支付 (Anti-Corruption Layer)
        # 用防腐层隔离第三方支付系统的复杂模型,保护内部领域纯净性
        paymentACL -> paymentGateway "适配外部支付\n(Anti-Corruption Layer)" "HTTPS/Webhook"
        
        # 订单 -> 外部物流 (Published Language)
        # 订单发布标准化的发货事件,物流系统订阅
        orderMQ -> logistics "发布发货事件\n(Published Language)" "AMQP" "Async"
        
        # CQRS同步
        orderMQ -> orderReadDB "投影订单读模型\n(Event Sourcing)" "AMQP" "Async"
    }

    views {
        # ========== System Context 视图 ==========
        systemContext ecommerce "SystemContext" "电商平台系统上下文 - 展示限界上下文边界" {
            include *
            autolayout lr
        }
        
        # ========== Container 视图 ==========
        container ecommerce "Containers" "电商平台容器架构 - 展示上下文内部结构与集成模式" {
            include *
            autolayout lr
        }
        
        # ========== 动态视图: 下单流程 ==========
        dynamic ecommerce "OrderFlow" "订单创建流程 - 展示上下文协作" {
            customer -> apiGateway "1. 提交订单"
            apiGateway -> orderAPI "2. 创建订单"
            orderAPI -> catalogAPI "3. 验证商品"
            orderAPI -> inventoryAPI "4. 预占库存"
            orderAPI -> orderDomain "5. 计算价格"
            orderDomain -> orderDB "6. 保存订单"
            orderDomain -> orderMQ "7. 发布OrderCreated事件"
            orderMQ -> orderReadDB "8. 更新查询模型"
            autolayout lr
        }
        
        # ========== 主题样式 ==========
        styles {
            element "Person" {
                shape Person
                background #08427B
                color #ffffff
            }
            element "External" {
                background #999999
                color #ffffff
            }
            element "Internal" {
                background #1168BD
                color #ffffff
            }
            element "API" {
                shape RoundedBox
                background #438DD5
                color #ffffff
            }
            element "Database" {
                shape Cylinder
                background #2E7D32
                color #ffffff
            }
            element "MessageBroker" {
                shape Pipe
                background #F57C00
                color #ffffff
            }
            element "DomainService" {
                shape Hexagon
                background #C2185B
                color #ffffff
            }
            element "AntiCorruptionLayer" {
                shape Component
                background #7B1FA2
                color #ffffff
            }
            element "Cache" {
                shape Cylinder
                background #D32F2F
                color #ffffff
            }
            element "SearchEngine" {
                shape Cylinder
                background #0288D1
                color #ffffff
            }
            element "Gateway" {
                shape RoundedBox
                background #512DA8
                color #ffffff
            }
            relationship "Async" {
                style Dashed
                color #F57C00
            }
            relationship "Sync" {
                style Solid
                color #1976D2
            }
        }
    }
    
    configuration {
        scope softwaresystem
    }
}