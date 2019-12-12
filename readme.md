
### 待完成任务
- [x] 加入配置 模拟正常场景同步
- [x] 修改maven冲突 改为锁定版本
- [ ] 增加流程图以及设计方案

### 打包
mvn clean package -Dmaven.test.skip=true

### 运行
nohup java -server -jar /apps/jars/data.jar > /dev/null 2>&1 &
