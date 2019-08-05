package com.pl23k.restaurant.controller;

import com.jfinal.core.Controller;
import org.apache.log4j.Logger;

/**
 * Created by HelloWorld on 2019-07-15.
 */
public class ShopController extends Controller {

    private static Logger logger = Logger.getLogger(ShopController.class);

    public void index(){
        logger.info("visit index.");
        /*
        Config config = Config.getConfigById(1);
        renderText(String.format("Hello %s!", config.getGameName()));
         */
    }
}
