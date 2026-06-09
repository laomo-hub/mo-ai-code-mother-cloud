# mo-ai-code-mother-cloud

独立的微服务版 AI 应用生成平台工程，拆分为用户、应用、AI、截图、公共模型和内部服务接口模块。

## 模块

- `mo-ai-code-common`: 通用响应、异常、配置、常量、COS、工具类
- `mo-ai-code-model`: Entity、DTO、VO、枚举
- `mo-ai-code-client`: Dubbo 内部服务接口
- `mo-ai-code-user`: 用户服务，端口 `8124`，Dubbo 端口 `50051`
- `mo-ai-code-app`: 应用服务和对外 API，端口 `8125`，Dubbo 端口 `50053`
- `mo-ai-code-ai`: AI 代码生成能力
- `mo-ai-code-screenshot`: 截图服务，端口 `8127`，Dubbo 端口 `50052`

## 依赖服务

本地运行前需要准备：

- JDK 21
- Maven 3.9+
- MySQL，数据库名 `mo_ai_code_mother`
- Redis
- Nacos，默认地址 `127.0.0.1:8848`
- 可选：Chrome/ChromeDriver，用于截图服务

初始化数据库：

```bash
mysql -uroot -p < sql/create_table.sql
```

## 配置

默认配置在各服务的 `src/main/resources/application.yml` 中。运行前至少需要确认：

- MySQL 账号密码
- Redis 地址和密码
- Nacos 地址
- `mo-ai-code-app` 中的大模型 API Key
- `mo-ai-code-screenshot` 中的腾讯云 COS 配置

## 构建

推荐使用本机 Maven：

```powershell
mvn clean package -DskipTests
```

项目也保留了 Maven wrapper；如果本机 wrapper 可用，也可以使用 `.\mvnw.cmd clean package -DskipTests`。

## 启动顺序

先启动基础设施 MySQL、Redis、Nacos，然后建议按以下顺序启动：

```bash
./mvnw -pl mo-ai-code-user spring-boot:run
./mvnw -pl mo-ai-code-screenshot spring-boot:run
./mvnw -pl mo-ai-code-app spring-boot:run
```

`mo-ai-code-app` 是主要对外 API 服务，接口前缀为 `http://localhost:8125/api`。
