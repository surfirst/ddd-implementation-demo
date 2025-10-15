# 请把下面的内容粘贴到下面的网址查看架构图,
# https://structurizr.com/dsl
# 或者在 VSCode 安装 C4 Model Extension 编辑和查看
workspace "XIoT Lite Architecture" {
    model {
        player = person "Player"

        pe = softwareSystem "Player Enrollment" "Player enrollment system" {
            ui = container "enrollment UI" "React" "" "UIContainer"
            svc = container "enrollment service" ".Net core"
            db = container "database" "SQL Server" "" "DBContainer"
            agent = container "IGT Patron Agent" "IGT PM ADI Service Agent"
            gw = container "Google Wallet Agent" "Agent for Google Wallet Service"
            img = container "Imaging Service" "Convert HTML to image so that user can save it to photo"
        }

        pm = softwareSystem "IGT Player Management" "" "Existing System"
        mailsvc = softwareSystem "Mail Server" "" "Existing System"
        apple = softwareSystem "Apple Pay" "" "Existing System"
        google = softwareSystem "Google Pay" "" "Existing System"

        ## PE containers
        player -> ui
        player -> img "Download player card image"

        ui -> svc "API"
        
        svc -> mailsvc "send email and encode card query"
        svc -> db "OTP info, enrollment log"

        ## 3rd party systems
        svc -> agent "IGT player enrollment and lookup"
        agent -> pm "enroll, lookup"
        apple -> svc "wallet ID"
        gw -> google "wallet ID"
        svc -> gw "Get 'Add to Wallet' link"

    }

    views {
        systemLandscape {
            include *
            autoLayout
        }

        container pe {
            include *
            autoLayout
        }

        styles {
            element "Software System" {
                background #1168bd
                color #ffffff
            }
            element "Person" {
                shape person
                background #08427b
                color #ffffff
            }
            element "Existing System" {
                background #999999
                color #ffffff
            }
            element "Container" {
                background #438dd5
                color #ffffff
            }
            element "Existing Container" {
                background #999999
                color #ffffff
            }
            element "Database" {
                shape cylinder
                background #999999
                color #ffffff
            }
            element "collector" {
                background #7BB0E6
                color #ffffff
            }
            element "DBContainer" {
                shape Cylinder
                background #999999
                color #ffffff
            }
            element "UIContainer" {
                shape WebBrowser
                background #6FACE9
                color #ffffff
            }
        
        }
    }
}