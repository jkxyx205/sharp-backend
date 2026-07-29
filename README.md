
# sharp-backend

简要说明：这是一个基于 Spring Boot 的后端示例工程，包含一个简单的 demo 模块，演示常见的分层结构（Controller / Service / DAO / Entity）、配置文件和日志配置。

**功能**
- **REST API 示例**：`demo` 模块提供基础的课程（Course）增删改查接口。
- **分层架构**：演示 Controller、Service、DAO 与 Entity 的典型组织方式。
- **环境配置**：包含 `application.yml` 以及 `application-dev.yml`、`application-prod.yml` 环境配置示例。
- **日志配置**：提供基于 Logback 的日志配置文件（见 `src/main/resources/logback`）。
- **单元测试示例**：包含测试用例 `TableGeneratorTest` 作为参考。

**快速开始（本地）**

**环境要求**
- **JDK**：JDK 8 及以上（推荐 JDK 17）
- **Gradle**：7.5.1（项目已自带 `gradlew`，无需单独安装）
- **数据库**：MySQL 8.0+ 或 PostgreSQL（凭个人偏好选择，默认配置在 `.env-dev.properties`）

**数据库初始化**
项目提供了两个测试类，用于从本地指定目录导入数据库备份 SQL（常用于本地恢复生产数据快照）：

- `DBInit`（MySQL）：读取 `/Users/rick/Space/Share` 目录下形如 `timeline-YYYY-MM-DD.sql` 的文件，自动创建新数据库并导入。需要修改 `DBInit.java` 中的 `folder` 路径和 `FILE_DATABASE_NAME` 以匹配你的环境。
- `DBInit2`（PostgreSQL）：逻辑同上，导入 `timeline-YYYY-MM-DD.sql`，并依赖 `.env-dev.properties` 配置文件。

说明：这两个类位于 `src/test/java/com/rick/backend/`，属于测试维护脚本，不会随应用启动执行，按需手动运行。

运行应用（使用 Gradle wrapper）：

```bash
./gradlew bootRun
```

构建 jar：

```bash
./gradlew clean build
```


**BaseApi 使用指南**
`BaseApi` 位于 `src/main/java/com/rick/backend/module/common/controller/BaseApi.java`，提供通用的增删改查与列表接口实现，控制器可以通过继承 `BaseApi` 快速暴露标准 REST 接口。

常用端点（基于子控制器 `@RequestMapping` 的路径前缀）：
- `GET /`：列表查询，返回分页 `Grid<Map<String,Object>>`（已扁平化联表字段名），支持查询参数（分页、过滤等，由 `GridUtils` 与表的 `selectConditionSQL` 决定）。
- `GET /detail`：级联详情列表，返回 `Grid<T>`，rows 为完整实体对象列表（根据 `selectByIds`）。
- `GET /one`：基于请求参数的单条查询，返回 `T`（若未命中抛出 `ResourceNotFoundException`）。
- `GET /new`：返回一个实体空实例（用于前端构建默认值）。
- `GET /{id}`：按主键查询单个实体。
- `POST /`：保存或更新实体（`insertOrUpdate`），请求体 `application/json`。
- `PUT /{id}`：按主键更新实体，路径 `id` 会设置到请求体实体上后执行 `update`。
- `DELETE /{id}`：删除指定主键实体，返回操作结果包装在 `Result<?>` 中。

示例：如果你已有 `CourseService` 与 `Course` 实体，可以这样定义控制器：

```java
@RestController
@RequestMapping("courses")
public class CourseApi extends BaseApi<CourseService, Course, Long> {
	public CourseApi(CourseService baseService) {
		super(baseService);
	}
}
```

示例请求：

```bash
# 列表
curl -sS "http://localhost:9090/courses"

# 详情列表
curl -sS "http://localhost:9090/courses/detail"

# 新建模板
curl -sS "http://localhost:9090/courses/new"

# 查询 id=1
curl -sS "http://localhost:9090/courses/1"

# 保存/更新（JSON body）
curl -sS -X POST "http://localhost:9090/courses" -H "Content-Type: application/json" -d '{"name":"新课程","teacher":"李老师"}'

# 删除 id=1
curl -sS -X DELETE "http://localhost:9090/courses/1"
```

扩展/注意事项：
- 控制器继承 `BaseApi` 后可以直接使用以上端点；若需自定义行为，可覆写对应方法或在子类中新增端点。
- `list` 返回的 Map 会调用 `flattenKeys` 做键扁平化（例如 `teacher.name` → `teacherName`），便于前端消费。
- 异常处理：未找到资源时会抛出 `ResourceNotFoundException`，请在全局异常处理器中统一转换为合适的 HTTP 返回。
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

**当前认证策略（Auth）**
- 所有接口默认通过拦截器校验 `token`，未携带或无效时返回 `401`。
- 登录接口：`POST /login`，请求体包含 `username` 和 `password`。
- 登录成功后返回一个 `token`，前端后续请求需在请求头中携带：`token: <token>`。
- 退出登录接口：`POST /logout`，同样通过 `token` 头进行鉴权，当前设备的 token 会被移除。
- 认证策略为“按设备绑定 token”：
  - 同一设备重复登录时，直接返回旧 token，并刷新它的过期时间。
  - 不同设备登录时，各自保留独立的 token，不会互相影响。
  - 只要 token 被正常使用，缓存有效期会被刷新，默认保持 120 分钟有效。

示例请求：

```bash
# 登录
curl -sS -X POST "http://localhost:9090/login" \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"123456"}'

# 访问受保护接口（替换为实际返回的 token）
curl -sS "http://localhost:9090/xxx" \
  -H "token: <your-token>"

# 当前设备退出登录
curl -sS -X POST "http://localhost:9090/logout" \
  -H "token: <your-token>"
```

如果需要区分设备，可以在登录请求中额外携带 `X-Device-Id` 头：

```bash
curl -sS -X POST "http://localhost:9090/login" \
  -H "Content-Type: application/json" \
  -H "X-Device-Id: device-001" \
  -d '{"username":"admin","password":"123456"}'
```

**用户密码修改**
- 新增接口：`POST /users/change-password`
- 请求体包含：`username`、`oldPassword`、`newPassword`
- 旧密码校验通过后，系统会更新为新的加密密码。

示例请求：

```bash
curl -sS -X POST "http://localhost:9090/users/change-password" \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","oldPassword":"123456","newPassword":"2222222"}'
```

**贡献**
- 欢迎 issue 与 PR。提交变更前请确保本地能通过 `./gradlew build`。

**联系 / 作者**
- 仓库维护者：Rick

