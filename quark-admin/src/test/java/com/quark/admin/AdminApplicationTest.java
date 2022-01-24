package com.quark.admin;

import com.quark.admin.service.*;
import com.quark.common.dao.AdminUserDao;
import com.quark.common.entity.AdminUser;
import com.quark.common.entity.Posts;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import javax.sql.DataSource;

/**
 * Created by lhr on 17-7-31.
 * user.setUsername("lhr");
 * user.setPassword("root");
 */
@RunWith(SpringRunner.class)
@TestPropertySource(locations = {"classpath:admin.properties"})
@SpringBootTest
public class AdminApplicationTest {

    @Resource
    DataSource dataSource;

    @Resource
    AdminUserService adminUserService;

    @Resource
    PermissionService permissionService;

    @Resource
    RoleService roleService;

    @Resource
    AdminUserDao dao;

    @Resource
    UserService userService;

    @Resource
    PostsService postsService;

    @Test
    public void testDataSource() {
       System.out.println(dao.findById(3).orElse(new AdminUser()));

    }



    @Test
    public void testRole(){
//        AdminUser user1 = new AdminUser();
//        AdminUser user2 = new AdminUser();
//        AdminUser user3 = new AdminUser();
//        user1.setId(11);
//        user2.setId(12);
//        user3.setId(13);
        adminUserService.saveAdminEnable(new Integer[]{11,12,13});
    }

    @Test
    public void testPosts(){
        Posts posts = new Posts();
//        posts.setId(1);
        posts.setTitle("测试");
        posts.setUser(userService.findOne(2));
        Page<Posts> page = postsService.findByPage(posts, 0, 10);
        System.out.println(page.getContent());
    }
}
