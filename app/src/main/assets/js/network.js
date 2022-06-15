var network = {
    callbackFunction: false,
    callback: function( data , params ){
        if( typeof this.callbackFunction === 'function' )
            this.callbackFunction( data , params );
    },
    get: function(url, success, noCache){
        noCache = noCache || false;
        
        this.callbackFunction = success;

        window.pachira.doGet(url, 'network.callback', !noCache);
    },
    post: function(url, params, success){
        this.callbackFunction = success;

        window.pachira.doRequest(url, 1, JSON.stringify(params), 'network.callback');
    },
    put: function(url, params, success){
        this.callbackFunction = success;

        window.pachira.doRequest(url, 2, JSON.stringify(params), 'network.callback');
    },
    dl: function(url, params, success){
        this.callbackFunction = success;

        window.pachira.doRequest(url, 3, JSON.stringify(params), 'network.callback');
    }
};

function drag( ele , left ){
    left = left || 72;
    var elements = document.getElementsByClassName(ele);
    for(var index in elements)
        listenPan(elements[index], left);
}

function listenPan( element, _left ){
    var hammer = new Hammer(element, {
        direction: 6
    });

    var imgs = $(element).find('img');
    var MAX_LENGTH = imgs.length * 110 + 5 * (imgs.length - 1);

    hammer.on('pan', function(ev) {
        var left = imgs.offset().left;
        var _length = 0;

        var deltaX = ev.deltaX / 6;
            
        if( deltaX > 0 ){
            _length = _left - left;
            _length = deltaX > _length ? _length : deltaX;
        }
        else if( deltaX < 0 ){
            _length = MAX_LENGTH - $(element).width() - _left + left + 10;
            _length = Math.abs(deltaX) > _length ? 0 - length : deltaX - 10;
        }
        else 
            _length = 0;
        
        $(element).find('img').css({
            left : "+=" + _length
        });
    });

    hammer.on('swipe', function(ev) {
        var left = imgs.offset().left;
        var _length = 0;

        var deltaX = 6 * ev.deltaX;
            
        if( deltaX > 0 ){
            _length = _left - left;
            _length = deltaX > _length ? _length : deltaX;
        }
        else if( deltaX < 0 ){
            _length = MAX_LENGTH - $(element).width() - _left + left + 30;
            _length = Math.abs(deltaX) > _length ? 0 - length : deltaX - 30;
        }
        else 
            _length = 0;
        
        $(element).find('img').css({
            left : "+=" + _length
        });
    });
}

function showCancel( className ){
    var elements = document.getElementsByClassName(className);

    for (var i = 0; i < elements.length; i++ ) {
        var element = elements[i];

        if( typeof element == 'number' || typeof element == 'function' )
            continue;

        cancelOperation( element );
    }
}

function cancelOperation( element ){
    var hammer = new Hammer(element, {
        direction: 6
    });

    hammer.on('swipe', function(ev) {
        var deltaX = ev.deltaX;

        if( deltaX > 0 )
            $(element).find('.content').animate({ left: 0 });
        else if( deltaX < 0 ){
            var width = $(element).find('.cancel').width();
            width += Number($(element).find('.cancel').css('paddingLeft').replace('px', ''));
            width += Number($(element).find('.cancel').css('paddingRight').replace('px', ''));

            $(element).find('.content').animate({ left: '-=' + width });
        }
    });
}

var Camera = {
    callbackName: null,
    callback: function( data ){
        if( typeof this.callbackName === 'function' )
            this.callbackName( data );
    },
    picture: function( callback ){
        this.callbackName = callback;
        window.pachira.camera( 'Camera.callback' );
    }
};