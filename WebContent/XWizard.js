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

    // Delegated, so that they also apply to buttons replaced by auto-draw.
    $(document).on('click', '.button:not(.question)', function (e) {
        if(!$(this).hasClass('mLDisabled')){ //Nur diese if-Bedingung ist neu, alles andere bleibt gleich
            $('.button').addClass("disabled");
            setTimeout("$('.button').removeClass('disabled')", 2000);
        }
    });
    
    $(document).on('click', '.button', function(){
        if($(this).hasClass('mLDisabled')){
            return false;
        }
    });
    $(document).on('click', '.modalLink', function(){
        if($(this).hasClass('mLDisabled')){
            return false;
        }
    });

    // Auto-draw: re-renders the script in the background 5 seconds after the last edit, but only if
    // it differs from what is currently drawn. Only the output and the conversion buttons are
    // replaced, so that editor cursor, focus and scroll position survive.
    var AUTO_DRAW_DELAY_MS = 5000;
    var AUTO_DRAW_STORAGE_KEY = 'xwizardAutoDraw';
    var autoDrawTimer = null;
    var autoDrawBox = $('#autoDraw');
    var lastDrawnScript = editor.getValue(); // The page was rendered from this script.

    function autoDrawStored(value) {
        try {
            if (value === undefined) {
                return window.localStorage.getItem(AUTO_DRAW_STORAGE_KEY) === 'true';
            }
            window.localStorage.setItem(AUTO_DRAW_STORAGE_KEY, value ? 'true' : 'false');
        } catch (e) {
            // Storage unavailable (e.g. private mode): the setting just is not remembered.
        }
        return false;
    }

    function scheduleAutoDraw() {
        clearTimeout(autoDrawTimer);
        if (autoDrawBox.prop('checked') && editor.getValue() !== lastDrawnScript) {
            autoDrawTimer = setTimeout(runAutoDraw, AUTO_DRAW_DELAY_MS);
        }
    }

    function runAutoDraw() {
        var scriptToDraw = editor.getValue();
        if (!autoDrawBox.prop('checked') || scriptToDraw === lastDrawnScript) {
            return;
        }
        // Hidden tabs are not drawn; try again later.
        if (document.hidden) {
            autoDrawTimer = setTimeout(runAutoDraw, AUTO_DRAW_DELAY_MS);
            return;
        }

        editor.save(); // Copy the CodeMirror content into the textarea that gets submitted.
        var form = autoDrawBox.closest('form');

        $.ajax({
            url: form.attr('action'),
            method: 'POST',
            data: form.serialize(),
            dataType: 'html'
        }).done(function (html) {
            lastDrawnScript = scriptToDraw;
            if (!autoDrawBox.prop('checked')) {
                return; // Switched off while the request was running.
            }
            var doc = new DOMParser().parseFromString(html, 'text/html');
            var newOutput = doc.getElementById('drawbox');
            if (!newOutput) {
                return; // Not a regular drawing (e.g. a plain text result): keep the current output.
            }
            $('#drawbox').html(newOutput.innerHTML);

            var newMethods = doc.querySelector('.conversionTable');
            var oldMethods = $('.conversionTable');
            if (newMethods && oldMethods.length) {
                oldMethods.first().replaceWith(newMethods.outerHTML);
            }

            Tipped.create('#drawbox .modalLink' + tooltipSelector, {position: 'topleft', skin: 'light'});
            Tipped.create('.conversionTable .button' + tooltipSelector, {position: 'topleft', skin: 'light'});
        }).always(scheduleAutoDraw); // Picks up edits made while the request was running.
    }

    if (autoDrawBox.length) {
        autoDrawBox.prop('checked', autoDrawStored());
        autoDrawBox.on('change', function () {
            autoDrawStored(this.checked);
            scheduleAutoDraw();
        });
        editor.on('change', scheduleAutoDraw);
    }

    $('.disabled').click(function(e){
        e.preventDefault();
    });
    
    $('#tabs').on('click', '.tab', function(e){

        var selectedE = $(e.target);
        $('.tab').removeClass("activeTab");
        selectedE.addClass("activeTab");
        hidetabs();
        var m = /\bbut(\d+)\b/.exec(selectedE.attr("class") || "");
        if (m) {
            $('#tab' + m[1]).css("display", "inherit");
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
        $('#tabcontent > div').css("display", "none");
    }

    function scrollTo(hash) {location.hash = "#" + hash;}
});