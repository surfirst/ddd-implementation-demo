这里展示了使用领域驱动设计(DDD)的分层架构和六边形架构实现的完整代码，供希望从数据库驱动的软件设计升级为以领域模型驱动的软件设计的架构师、高级开发者参考。

## C4 架构架构图

项目的 C4 架构图可以参考 [`doc-demos/arc/ecms-cc.dsl`](doc-demos/arc/ecms-cc.dsl) 这个示例进行设计。使用 https://structurizr.com/dsl 进行设计，然后导出和预览 C4 架构图。


## 分层架构和六边形架构示例
### 架构图
`demos/enrollment/architecture/player-enrollment.dsl` 则提供了报名示例的 C4 架构图定义，可直接加载以对照代码理解上下文边界和交互。

### 代码示例
`demos/enrollment/` 提供了一个围绕报名上下文的完整示例，展示如何在实践中结合 DDD 的分层设计与六边形架构：
- **领域模型优先**：在 `Domain` 层聚焦核心概念与不变量，通过聚合、实体和值对象保持业务语义的一致性。
- **应用服务编排**：`Application` 层仅负责协调用例流程与跨上下文交互，将复杂领域逻辑委托给领域对象。
- **防腐与适配**：`Infrastructure` 层通过适配器隐藏外部系统（如钱包接口）的细节，实现六边形架构的端口与适配器模式，避免侵入核心模型。
- **领域守护测试**：`demos/enrollment/service/application/src/test/java/com/example/enrollment/application/registration/RegistrationServiceTest.java` 覆盖核心报名场景，验证并守护领域模型行为。


