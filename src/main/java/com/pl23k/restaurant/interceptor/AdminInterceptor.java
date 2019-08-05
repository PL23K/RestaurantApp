package com.pl23k.restaurant.interceptor;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.core.Controller;
import com.jfinal.kit.StrKit;
import com.pl23k.restaurant.common.SystemConfig;
import com.pl23k.restaurant.controller.AdminController;
import com.pl23k.restaurant.model.Admin;

/**
 * Created by PL23K on 2019-01-12.
 */
public class AdminInterceptor implements Interceptor {
    @Override
    public void intercept(Invocation invocation) {
        Controller controller = invocation.getController();
        String loginAdmin = controller.getSessionAttr(AdminController.SESSION_ADMIN_USERNAME);

        Admin admin = Admin.getAdminByUsername(loginAdmin);
        if(StrKit.isBlank(loginAdmin) || admin==null){
            controller.redirect("/"+ SystemConfig.sAdminAddr+"/login");
        }else{
            controller.setAttr("admin",admin);
            invocation.invoke();
        }
    }
}
