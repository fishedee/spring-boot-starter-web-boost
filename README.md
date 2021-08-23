![Release](https://jitpack.io/v/fishedee/spring-boot-starter-jpa-boost.svg)
(https://jitpack.io/#fishedee/spring-boot-starter-jpa-boost)

# jpa_boost

JPA的Curd存储库，与查询存储库，功能：

* 简单，无配置生成数据存储库
* 任意查询，支持任意筛选，包括子数据join的查询。

## 安装

```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>

<dependency>
    <groupId>com.github.fishedee</groupId>
    <artifactId>spring-boot-starter-jpa-boost</artifactId>
    <version>1.0</version>
</dependency>
```

在项目的pom.xml加入以上配置即可

## 使用

代码在[这里](https://github.com/fishedee/spring-boot-starter-jpa-boost/tree/master/spring-boot-starter-jpa-boost-sample)

```ini
# 开启jpa-boost
spring.jpa-boost.enable=true
```

初始化配置

```java
package com.fishedee.jpa_boost.sample;

import lombok.Getter;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@ToString
@Getter
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;

    private Long age;

    protected User(){

    }

    public User(String name,Long age){
        this.name = name;
        this.age = age;
    }

    public void setName(String name){
        this.name = name;
    }
}
```

定义一个User实体

```java
package com.fishedee.jpa_boost.sample;

import com.fishedee.jpa_boost.CurdFilterBuilder;
import com.fishedee.jpa_boost.CurdPageAll;
import com.fishedee.jpa_boost.CurdRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserRepository extends CurdRepository<User,Long> {
    public UserRepository(){
        super("用户");
    }

    public List<User> getByName(String name){
        //使用CurdFilterBuilder来做任意查询
        CurdFilterBuilder builder = new CurdFilterBuilder();
        builder.like("name","%"+name+"%");
        return this.findByFilter(builder, new CurdPageAll(),false,false).getData();
    }
}
```

定义一个User实体的存储库，默认就有Curd操作

```java
@SpringBootTest
@DirtiesContext(classMode= DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class CurdTest {
    @Autowired
    private UserRepository userRepository;

    @Test
    public void testGetAll(){
        List<User> userList = userRepository.getAll();
        assertEquals(userList.size(),0);
    }

    //修改操作的，需要有@Transicational注解
    @Test
    @Transactional
    public void testCurd(){
        //添加
        User user = new User("fish",123L);
        User user2 = new User("cat",456L);
        userRepository.add(user);
        userRepository.add(user2);

        List<User> userList = userRepository.getAll();
        assertEquals(userList.size(),2);

        //查询
        List<User> iNameList = userRepository.getByName("i");
        JsonAssertUtil.checkEqualStrict("[{id:1,name:\"fish\",age:123}]",iNameList);

        //删除
        userRepository.del(user);
        List<User> userList2 = userRepository.getAll();
        assertEquals(userList2.size(),1);
        assertEquals(userList2.get(0).getName(),"cat");

        //修改
        user2.setName("mk");
        List<User> userList3 = userRepository.getAll();
        assertEquals(userList3.size(),1);
        assertEquals(userList3.get(0).getName(),"mk");
    }
}
```

User实体的Curd测试
