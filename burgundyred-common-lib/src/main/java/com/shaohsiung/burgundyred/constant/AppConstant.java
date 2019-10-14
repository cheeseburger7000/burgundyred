package com.shaohsiung.burgundyred.constant;

public interface AppConstant {
    /** 用户默认头像*/
    String DEFAULT_AVATAR_PATH = "/images/avatar/default_profile.png";

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
}