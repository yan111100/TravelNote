// var WIDTH = $(window).width();
var WIDTH = window.pachira.getWidth() / window.devicePixelRatio;
$('body').width(WIDTH);
var Slider = {
    id: 0,
    slider : function ( $slider , reset){
        reset = reset || false;
        var itemWidth = $slider.width();

        var lis = $slider.find('li');
        $('.dots').children('span').removeClass('active');
        $.each(lis, function(k, li){
            if( $(li).offset().left < 0 ){
                $(li).css({
                    top: 0,
                    left: lis.length * itemWidth + $(li).offset().left
                });
                $('.dots').children('span:eq(' + ((k + 1) % lis.length) + ')').addClass('active');
            }
            else if( reset )
                $(li).css({
                    top: 0,
                    left: k * itemWidth
                });
        });
    },
    sliderAnimate: function ($slider, callback){
        var lis = $slider.find('li');
        var itemWidth = $slider.width();

        var index = 0;
        var fact = this;
        $slider.find('li').animate({
            left: "-=" + itemWidth,
        },
        500,
        function(){
            index++;
            if( typeof callback === 'function' )
                callback();

            if( index === lis.length )
                fact.slider($slider);
        });
    },
    start: function ($slider){
        var length = $slider.find('li').length;
        var itemWidth = $slider.width();
        this.slider($slider, true);
        var fact = this;
        if( this.id > 0 )
            clearInterval(this.id);
        if( $slider.find('li').length > 1 ){
            var id = setInterval(function(){
                fact.sliderAnimate($slider);
            }, 3000);
            this.id = id;
        }

        var dots = [];

        for(var i = 0; i < $slider.find('li').length; i++){
            if( i == 0 )
                dots.push('<span class="active"></span>');
            else
                dots.push('<span></span>');
        }

        $('.dots').html(dots.join(''));
    }
};