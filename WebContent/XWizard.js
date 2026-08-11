//document.ready - Funktionen werden nicht aufgerufen, bevor das Dokument nicht geladen ist
$(function() {
	
    Tipped.create('.modalLink', {
        position: 'topleft',
        skin: 'light'
    });
    Tipped.create('.button', {
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
        $("#nav1").slideToggle("slow");
    });
    $('#wrapper').click(function() {
        var display = $('#menubutton').css('display');
        if(display != 'none'){
            $("#nav1").slideUp("fast");
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
        if(selectedE.hasClass("but1")){
            $('#tab1').css("display", "inherit");
        }
        else if(selectedE.hasClass("but2")){
            $('#tab2').css("display", "inherit");
        }
        else if(selectedE.hasClass("but3")){
            $('#tab3').css("display", "inherit");
        }
        else if(selectedE.hasClass("but4")){
            $('#tab4').css("display", "inherit");
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
    }

    function scrollTo(hash) {location.hash = "#" + hash;}
});