package service;

import dao.UserDAO;
import model.User;

public class UserService {

    private UserDAO userDAO;

    public UserService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }


    // 登录方法，验证用户名和密码
    public User loginUser(String account, String password) {

        return userDAO.getUserInfo(account);

//        // 通过用户名获取用户信息
//        User user = userDAO.getUserInfo(account);
//        System.out.println("password" + password);
//        System.out.println("user.getpassword" + user.getPassword());
//        // 如果找到了用户，且密码匹配
//        if (user != null && user.getPassword().equals(password)) {
//            return user;  // 返回匹配的用户
//        }
//
//        // 如果没有匹配的用户或密码错误，返回null
//        return null;
    }
    public boolean authenticateUser(String account, String password) {
        return userDAO.loginUser(account, password);
    }


    // 注册方法
    public boolean register(User user) {
        // 可以先校验用户是否已经存在
        if (userDAO.findUserByAccount(user.getAccount()) != null) {
            System.out.println("账号已存在");
            return false;
        }
        return userDAO.registerUser(user);
    }

    // 获取用户信息
    public User getUserInfo(String account) {
        return userDAO.getUserInfo(account);
    }

    // 删除用户
    public boolean deleteUser(int account) {
        return userDAO.deleteUser(account);
    }

    // 根据账户查找用户
    public User findUserByAccount(String account) {
        return userDAO.findUserByAccount(account);
    }
}
