var mySwiper = new Swiper ('.swiper-container', {
    // on:{
    //     autoplay:function(){
    //         html = '<a class="ad-tip" href="/about/cooperation/index.html" target="_blank" ><span>Swiper </span>骞垮憡</a><a href="https://item.taobao.com/item.htm?id=603535476493" target="_blank"><img src="/templets/default/images/swiperAd1.png"></a>';
    //         $('.swiperAd').html(html)
    // },

    loop: true, // 循环模式选项
    
    // 如果需要分页器
    pagination: {
    el: '.swiper-pagination',
    },
    
    // 如果需要前进后退按钮
    navigation: {
    nextEl: '.swiper-button-next',
    prevEl: '.swiper-button-prev',
    },
})   