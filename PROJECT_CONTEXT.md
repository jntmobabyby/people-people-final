# PROJECT_CONTEXT

## 项目概况

项目名称：实验室设备预约与归还系统

项目路径：

```text
D:\edge\CodexProject\软件人人作业\lab-reservation-system
```

这是一个课程作业演示项目，采用前后端分离结构：

- 后端：Spring Boot 3 + MyBatis-Plus + MySQL
- 前端：Vue 3 + Vite + Element Plus
- 数据库：MySQL，默认库名 `lab_reservation`
- 后端端口：`8080`
- 前端端口：`5173`

项目目标是完成设备查询、预约提交、管理员审批、归还登记、维修上报和基础测试，不追求复杂权限系统。

## 测试账号

```text
管理员：admin / 123456
学生：stu001 / 123456
学生：stu002 / 123456
```

## 一键运行

在 PowerShell 中进入项目根目录：

```powershell
cd D:\edge\CodexProject\软件人人作业\lab-reservation-system
powershell.exe -ExecutionPolicy Bypass -File .\start-all.ps1
```

启动成功后访问：

```text
http://localhost:5173
```

停止前后端：

```powershell
powershell.exe -ExecutionPolicy Bypass -File .\stop-all.ps1
```

停止前后端和 MySQL：

```powershell
powershell.exe -ExecutionPolicy Bypass -File .\stop-all.ps1 -IncludeMySql
```

## 本机环境路径

当前机器上常用路径如下：

```text
JDK 17: D:\JDK17\jdk-17.0.19+10
Maven: D:\Maven\apache-maven-3.9.15
MySQL: D:\MySQL\mysql-8.4.0-winx64
MySQL 配置: D:\MySQL\my.ini
```

如果 `java`、`mvn`、`mysql` 命令无法识别，可以使用完整路径：

```powershell
& "D:\JDK17\jdk-17.0.19+10\bin\java.exe" -version
& "D:\Maven\apache-maven-3.9.15\bin\mvn.cmd" test
& "D:\MySQL\mysql-8.4.0-winx64\bin\mysql.exe" -uroot -proot -e "SHOW DATABASES;"
```

## 项目结构

```text
backend/       Spring Boot 后端
frontend/      Vue 3 前端
doc/sql/       MySQL 建表和初始化数据
README.md      部署和接口说明
PROJECT_CONTEXT.md 当前交接说明
```

## 关键业务规则

预约状态：

```text
0 待审批
1 已通过
2 已取消
3 已完成
4 已拒绝
```

设备状态：

```text
0 停用
1 可预约
2 占用中
3 维修中
```

重要规则：

- 学生提交预约后，预约状态为 `待审批`。
- 管理员审批通过后，只把预约记录状态改为 `已通过`。
- 管理员审批通过后，不应把设备永久改成 `占用中`，否则其他时间段无法预约。
- 设备能否被预约主要由设备状态和预约时间冲突共同决定。
- 预约冲突规则：同一设备下，如果 `new_start < old_end` 且 `new_end > old_start`，则拒绝预约。
- 相邻时间段不算冲突，例如 `10:00-12:00` 和 `12:00-14:00` 可以同时存在。
- 归还登记后，预约状态变为 `已完成`，设备状态恢复为 `可预约`。
- 上报维修工单后，设备状态变为 `维修中`。

## 关键后端文件

```text
backend/src/main/java/com/example/labreservation/service/ReservationService.java
```

负责预约创建、冲突校验、审批和取消。审批逻辑在这里，注意不要让审批把设备永久改为不可预约。

```text
backend/src/main/java/com/example/labreservation/service/ReturnService.java
```

负责归还登记，归还成功后把预约改为已完成，并把设备恢复为可预约。

```text
backend/src/main/java/com/example/labreservation/service/MaintenanceService.java
```

负责维修工单，上报维修后把设备状态改为维修中。

```text
backend/src/main/java/com/example/labreservation/domain/ReservationStatus.java
backend/src/main/java/com/example/labreservation/domain/EquipmentStatus.java
```

状态常量定义。

```text
backend/src/test/java/com/example/labreservation/LabReservationApplicationTests.java
```

后端核心测试，包括登录、预约、冲突、审批、归还、维修。

## 关键前端文件

```text
frontend/src/views/LoginView.vue
```

登录页。

```text
frontend/src/views/EquipmentView.vue
```

设备列表页。显示设备状态，管理员可以新增设备和修改设备状态。

```text
frontend/src/views/ReservationCreateView.vue
```

学生提交预约页。

```text
frontend/src/views/MyReservationsView.vue
```

学生查看自己的预约状态。

```text
frontend/src/views/AdminReservationsView.vue
```

管理员审批预约页。

```text
frontend/src/views/MaintenanceView.vue
```

维修工单页。

```text
frontend/src/api.js
```

Axios 封装，统一处理 `/api` 请求和错误提示。

```text
frontend/src/session.js
```

登录状态保存在 localStorage，key 为 `lab-reservation-user`。

```text
frontend/src/router.js
```

前端路由和简单登录守卫。

## 数据库文件

```text
doc/sql/schema.sql
doc/sql/data.sql
```

如果要重置数据库，可重新导入这两个文件。注意这会清掉现有预约数据。

MySQL 客户端完整路径示例：

```powershell
& "D:\MySQL\mysql-8.4.0-winx64\bin\mysql.exe" -uroot -proot < "D:\edge\CodexProject\软件人人作业\lab-reservation-system\doc\sql\schema.sql"
& "D:\MySQL\mysql-8.4.0-winx64\bin\mysql.exe" -uroot -proot < "D:\edge\CodexProject\软件人人作业\lab-reservation-system\doc\sql\data.sql"
```

如果中文路径导入失败，可以先把 SQL 文件复制到英文路径再导入。

## 验证命令

后端测试：

```powershell
cd D:\edge\CodexProject\软件人人作业\lab-reservation-system\backend
& "D:\Maven\apache-maven-3.9.15\bin\mvn.cmd" test
```

前端构建：

```powershell
cd D:\edge\CodexProject\软件人人作业\lab-reservation-system\frontend
npm run build
```

后端打包：

```powershell
cd D:\edge\CodexProject\软件人人作业\lab-reservation-system\backend
& "D:\Maven\apache-maven-3.9.15\bin\mvn.cmd" package -DskipTests
```

如果打包时报无法重命名 jar，通常是后端正在运行，占用了：

```powershell
cd D:\edge\CodexProject\软件人人作业\lab-reservation-system
powershell.exe -ExecutionPolicy Bypass -File .\stop-all.ps1
```

然后重新打包。

## 常见问题

### 浏览器显示 localhost 拒绝连接

通常是前端没有启动。运行：

```powershell
cd D:\edge\CodexProject\软件人人作业\lab-reservation-system
powershell.exe -ExecutionPolicy Bypass -File .\start-all.ps1
```

### `mvn` 不是可识别的命令

使用完整路径：

```powershell
& "D:\Maven\apache-maven-3.9.15\bin\mvn.cmd" test
```

### `java` 不是可识别的命令

使用完整路径：

```powershell
& "D:\JDK17\jdk-17.0.19+10\bin\java.exe" -version
```

### `mysql` 不是可识别的命令

使用完整路径：

```powershell
& "D:\MySQL\mysql-8.4.0-winx64\bin\mysql.exe" -uroot -proot -e "SHOW DATABASES;"
```

### 管理员看不到预约审批菜单

先确认使用的是管理员账号：

```text
admin / 123456
```

如果仍异常，浏览器清理 localStorage 中的 `lab-reservation-user`，重新登录。

管理员审批页地址：

```text
http://localhost:5173/admin/reservations
```

## 给后续智能体的注意事项

- 先读本文件，再读 `README.md` 和关键源码文件。
- 保持项目简单，不要引入复杂 JWT、Spring Security 或大型权限系统。
- 修改预约相关逻辑后，必须运行后端测试。
- 修改前端页面后，必须运行 `npm run build`。
- 不要把审批通过理解成设备永久不可预约；预约是否冲突要按时间段判断。
- 这是课程作业项目，代码以清晰、可运行、容易演示为优先。

## Git 和 PR 作业要求提醒

当前源码可以运行，但一次性生成源码不等于真实 Git Flow 或 Feature Branch 记录。

如果老师要求检查 Git/PR 证据，需要由你和小明后续真实完成：

- 创建 `feature/*` 分支。
- 分模块提交代码。
- 推送到远程仓库。
- 创建 Pull Request。
- 另一人 review 并 approve。
- 合并 PR 到主分支。

这些 GitHub 或 Gitee 页面截图、PR 链接、提交记录，才是“真实 PR”和“真实 feature 分支开发记录”。
