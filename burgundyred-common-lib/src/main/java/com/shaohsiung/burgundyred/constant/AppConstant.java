package com.shaohsiung.burgundyred.constant;

public interface AppConstant {
    /** TODO 会员默认头像 */
    String DEFAULT_AVATAR_PATH = "http://pzceg9ngw.bkt.clouddn.com/bc6a10a1-3797-4d8a-994b-6facc513ffc5";

    /** 用户激活 token key 前缀 */
    String USER_ACTIVATE_PREFIX = "user_activate_key_";

    /** redis购物车key*/
    String CART_KEY = "burgundyred_AppConstant.CART_KEY";

    /** 轮播图数量最大限制 */
    Integer MAX_BANNER_COUNT = 3;

    /** 轮播图缓存key */
    String BANNER_CACHE_KEY = "banner_cache_key";

    Integer PRODUCT_PAGE_SIZE = 4;

    Integer BANNER_PAGE_SIZE = 4;

    Integer CATEGORY_PAGE_SIZE = 4;

    Integer USER_PAGE_SIZE = 4;

    Integer ORDER_PAGE_SIZE = 4;

    String JWT_COOKIE_NAME = "token";

    String ADMIN_JWT_COOKIE_NAME = "admin_token";
}