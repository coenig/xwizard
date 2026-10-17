//document.ready - Funktionen werden nicht aufgerufen, bevor das Dokument nicht geladen ist
$(function() {
	
    // Tooltips rely on hover. On touch screens the first tap on an active button would only open the
    // tooltip (and iOS then swallows the click), so there they are limited to disabled buttons,
    // which do nothing on click anyway.
    var tooltipSelector = (window.matchMedia && window.matchMedia('(hover: hover) and (pointer: fine)').matches)
        ? ''
        : '.mLDisabled';
    Tipped.create('.modalLink' + tooltipSelector, {
        position: 'topleft',
        skin: 'light'
    });
    Tipped.create('.button' + tooltipSelector, {
        position: 'topleft',
        skin: 'light'
    });
    var codetextarea = $("#txtarea");
    var config = {
        mode: "stex",
        lineNumbers: true,
        viewportMargin: Infinity,
        lineWrapping: true
    };
    var editor = CodeMirror.fromTextArea(codetextarea.get(0), config);
    /*$('#nav1').on('click', '.nav-item', function(event) {
        var className = $(event.target).attr('class');
        console.log(className);    if(className.indexOf("external") > -1) {
            window.open(event.target.href, "_self");
        }
        event.preventDefault();
        var hash = this.hash;
        var display = $('#menubutton').css('display');
        if(display != 'none'){
            $("#nav1").hide(); //slideUp("slow");
        }
        $('html, body').animate({
            scrollTop: $(hash).offset().top
        }, 800, function() {
            window.location.hash = hash;
        });
    });
	*/
    //handled die Links der Navbar
    //Falls nav-item die Klasse "external" besitzt, wird der Link im selben Tab/Fenster aufgerufen
    //Sonst wird nur gescrollt
    $('#nav1').on('click', '.nav-item', function(event) {
        var className = $(event.target).attr('class');
        console.log(className);

        if(className.indexOf("external") > -1) {
            window.open(event.target.href, "_self");
        }

        event.preventDefault();
        var hash = this.hash;
        var display = $('#menubutton').css('display');
        if(display != 'none'){
            $("#nav1").hide(); //slideUp("slow");
        }
        $('html, body').animate({
            scrollTop: $(hash).offset().top
        }, 800, function() {
            window.location.hash = hash;
        });
    });

    /*Handled die In-Text Verlinkungen:
    Falls ein a-Element (neben der Klasse "simpleLink") die Klasse "external" besitzt, wird dieser Link in einem
    neuen Tab geoeffnet.
    Mailto wird beruecksichtigt
    Elemente, die lediglich die Klasse "simpleLink" besitzen, sind Links die auf die aktuelle Seite verweisen, es wird
    also nur gescrollt
    Zusaetzlich: Falls auf das .gif geklickt wird, wird auch hier gescrollt
    */
    $('.simpleLink').click(function(event) {
        var className = $(event.target).attr('class');
        var tagname = $(event.target).get(0).tagName;
        console.log(className + ", "+tagname);

            //"Normaler" Link
        if (tagname === "A") {
                //nav-item-Elemente sollen in diesem Listener ignoriert werden
            if(className.indexOf("nav-item") == -1){
                    //Externe Links
                if (className.indexOf("external") > -1) {
                    //Falls Link ein mailto sein sollte
                    if (event.target.href.indexOf("mailto") == -1) {
                        event.preventDefault();
                        window.open(event.target.href, "_blank");
                        window.focus();
                    }
                }
                //Externe Links, die aber auf die Seite zeigen und deshalb im selben Tab geöffnet werden sollen
                else if (className.indexOf("internal") > -1){
                	window.open(event.target.href, "_self");
                	
                }
                    //Interne Links zum scrollen
                else {
                    event.preventDefault();

                    var hash = this.hash;
                    $('html, body').animate({
                        scrollTop: $(hash).offset().top
                    }, 800, function () {
                        window.location.hash = hash;
                    });
                }
            }

        }
            //Bild
        else if(tagname === "IMG"){
            var hash = "#OOutput";
            $('html, body').animate({
                scrollTop: $(hash).offset().top
            }, 800, function () {
                window.location.hash = hash;
            });
        }
    });

    $('#menubutton').click(function(event) {
        event.preventDefault(); // do not jump to #nav1
        $("#nav1").slideToggle("slow");
    });
    // 'touchstart' because iOS does not fire 'click' on non-interactive elements
    $('#wrapper').on('click touchstart', function() {
        var display = $('#menubutton').css('display');
        if(display != 'none'){
            $("#nav1").slideUp("fast");
        }
    });
    // slideToggle leaves an inline display style behind that would hide/show the nav wrongly
    // after crossing the mobile breakpoint (e.g. rotating). Mobile browsers also fire 'resize'
    // when their address bar collapses, so only react to an actual breakpoint change.
    var wasMobileLayout = $('#menubutton').css('display') != 'none';
    $(window).on('resize', function() {
        var isMobileLayout = $('#menubutton').css('display') != 'none';
        if (isMobileLayout != wasMobileLayout) {
            wasMobileLayout = isMobileLayout;
            $("#nav1").css('display', '');
        }
    });

    //function enable(){
    //  $('.button').removeClass("disabled");
    //}

    $('.button').not('.question').click(function (e) {
        if(!$(this).hasClass('mLDisabled')){ //Nur diese if-Bedingung ist neu, alles andere bleibt gleich
            $('.button').addClass("disabled");
            setTimeout("$('.button').removeClass('disabled')", 2000);
        }
    });
    
    $('.button').bind('click', function(){
        if($(this).hasClass('mLDisabled')){
            return false;
        }
    });
    $('.modalLink').bind('click', function(){
        if($(this).hasClass('mLDisabled')){
            return false;
        }
    });

    $('.disabled').click(function(e){
        e.preventDefault();
    });
    
    $('#tabs').on('click', '.tab', function(e){

        var selectedE = $(e.target);
        $('.tab').removeClass("activeTab");
        selectedE.addClass("activeTab");
        hidetabs();
        for (var i = 1; i <= 5; i++) {
            if(selectedE.hasClass("but" + i)){
                $('#tab' + i).css("display", "inherit");
            }
        }
    });
    
    if($('.button.active').length){
    	setCorrectTab();
    }
    
    function setCorrectTab(){
        var selectedTabID = $('.button.active').closest('div').attr('id');
        var id = selectedTabID.substr(selectedTabID.length - 1);
        var tabStr = ".tab.but" + id.toString();
        $(tabStr).click();
        //$(tabStr).addClass("newClass");
        //hidetabs();
        //$("#"+selectedTabID).css('display', 'inherit');
    }

    function hidetabs(){
        $('#tab1').css("display", "none");
        $('#tab2').css("display", "none");
        $('#tab3').css("display", "none");
        $('#tab4').css("display", "none");
        $('#tab5').css("display", "none");
    }

    function scrollTo(hash) {location.hash = "#" + hash;}
});