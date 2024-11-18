package dao;

import model.User;

public interface UserDAO {
    // 登录用户
    boolean loginUser(String account, String password);

    // 注册用户
    boolean registerUser(User user);

    // 获取用户信息
    User getUserInfo(String account);


    // 删除用户
    boolean deleteUser(int userid);

    // 根据账户查找用户
    User findUserByAccount(String account);

    // 根据userid查账号
}
