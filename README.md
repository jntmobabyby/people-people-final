# 实验室设备预约与归还系统

本项目是人人结对作业的最小可交付源码，采用前后端分离结构：

- 后端：Spring Boot 3 + MyBatis-Plus + MySQL
- 前端：Vue 3 + Vite + Element Plus
- 数据库：MySQL 8

> 说明：当前源码是一次性生成的可运行版本。提交课程作业前，你和小明仍需要按 `feature/*` 分支和 Pull Request 重新组织真实开发记录，并在 PR 中完成另一人的复审和批准。

## 目录结构

```text
lab-reservation-system/
  backend/      Spring Boot 后端
  frontend/     Vue 前端
  doc/sql/      建表脚本和测试数据
  README.md     部署说明
```

## 数据库初始化

```sql
SOURCE doc/sql/schema.sql;
SOURCE doc/sql/data.sql;
```

或者手动在 MySQL 中依次执行：

```sql
CREATE DATABASE lab_reservation DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE lab_reservation;
```

然后执行 `doc/sql/schema.sql` 和 `doc/sql/data.sql`。

## 后端启动

```bash
cd backend
# 修改 src/main/resources/application.yml 中的数据库账号密码
mvn clean package
java -jar target/lab-reservation-system.jar
```

开发模式：

```bash
cd backend
mvn spring-boot:run
```

后端默认地址：

```text
http://localhost:8080/api
```

## 前端启动

```bash
cd frontend
npm install
npm run dev
```

前端默认地址：

```text
http://localhost:5173
```

## 测试账号

| 角色 | 账号 | 密码 |
|---|---|---|
| 管理员 | admin | 123456 |
| 普通学生 | stu001 | 123456 |
| 普通学生 | stu002 | 123456 |

## 核心接口

| 场景 | 接口 | 说明 |
|---|---|---|
| 登录 | `POST /api/auth/login` | 返回用户信息和演示 token |
| 设备查询 | `GET /api/equipment` | 支持名称、分类、状态筛选 |
| 提交预约 | `POST /api/reservations` | 校验设备状态和时间冲突 |
| 审批预约 | `POST /api/reservations/{id}/approve` | 管理员审批待审批预约 |
| 取消预约 | `POST /api/reservations/{id}/cancel` | 学生取消自己的预约 |
| 登记归还 | `POST /api/returns` | 生成归还记录并恢复设备状态 |
| 上报维修 | `POST /api/maintenance-tickets` | 生成维修工单并把设备置为维修中 |

## 预约冲突规则

同一设备下，如果新预约满足：

```text
new_start < old_end && new_end > old_start
```

则认为时间段重叠，系统拒绝提交预约。相邻时间段，例如 `10:00-12:00` 和 `12:00-14:00`，允许预约。

## 后端测试

```bash
cd backend
mvn test
```

测试覆盖登录、密码错误、维修中不可预约、正常预约、重叠预约失败、相邻预约成功、归还恢复设备状态、维修上报改变设备状态。

## 建议的真实 PR 拆分

为了满足课程要求，建议你和小明把当前源码按下面顺序重新提交到远程仓库：

1. `feature/init-project`：项目结构、README、SQL。
2. `feature/user-auth`：登录接口和前端登录页。
3. `feature/equipment-manage`：设备接口和设备页面。
4. `feature/reservation`：预约接口、冲突校验、预约页面。
5. `feature/return-maintenance`：归还和维修工单。
6. `feature/integration-test`：后端测试和走查修复。

每个 PR 都要由另一名成员 review，并在 PR 评论里留下真实改进意见或测试补充建议。
