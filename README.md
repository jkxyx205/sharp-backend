
# sharp-backend

简要说明：这是一个基于 Spring Boot 的后端示例工程，包含一个简单的 demo 模块，演示常见的分层结构（Controller / Service / DAO / Entity）、配置文件和日志配置。

**功能**
- **REST API 示例**：`demo` 模块提供基础的课程（Course）增删改查接口。
- **分层架构**：演示 Controller、Service、DAO 与 Entity 的典型组织方式。
- **环境配置**：包含 `application.yml` 以及 `application-dev.yml`、`application-prod.yml` 环境配置示例。
- **日志配置**：提供基于 Logback 的日志配置文件（见 `src/main/resources/logback`）。
- **单元测试示例**：包含测试用例 `TableGeneratorTest` 作为参考。

**快速开始（本地）**
前提：安装 JDK 与 Gradle（或使用项目自带的 `gradlew`）。

运行应用（使用 Gradle wrapper）：

```bash
./gradlew bootRun
```

构建 jar：

```bash
./gradlew clean build
```

运行测试：

```bash
./gradlew test
```

（可选）使用脚本：仓库包含 `mvn.sh`、`gradlew` 等，请根据偏好选择构建工具。

**配置**
- 配置文件位置：`src/main/resources/application.yml`（以及 `application-dev.yml` / `application-prod.yml`）。
- 日志配置：`src/main/resources/logback-spring.xml` 与 `src/main/resources/logback/` 下的文件。

**代码结构**
- 主程序入口：`src/main/java/com/rick/backend/BackendApplication.java`
- 模块目录：`src/main/java/com/rick/backend/module/demo/`（包含 `controller`, `service`, `dao`, `entity`）

**开发说明**
- 新增接口：在 `controller` 下创建新的 Controller；业务逻辑放在 `service`，数据访问放在 `dao`。
- 按需在 `application-*.yml` 中添加环境特定配置，运行时通过 `--spring.profiles.active` 指定。

**贡献**
- 欢迎 issue 与 PR。提交变更前请确保本地能通过 `./gradlew build`。

**联系 / 作者**
- 仓库维护者：Rick

