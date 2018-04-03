


$(function(){


  var stateSpaceUrl = './data/statespace.json';
  var heapConfUrl = './data/';

  if(location.search.indexOf('cex=')>=0) {
    var url_string = window.location.href;
    var url = new URL(url_string);
    var cex = url.searchParams.get("cex");
    stateSpaceUrl = './cex_' + cex + '/statespace.json'
    heapConfUrl = './cex_' + cex + '/'
  }



  var layoutPadding = 150;
  var hcLayoutPadding = 50;
  var aniDur = 500;
  var easing = 'linear';

  var cy;
  var cy2;

  var lastHighlightedNodes = [];
  var lastHighlightedNodesPositions = [];

  // get exported json from cytoscape desktop via ajax
  var graphP = $.ajax({
    url: stateSpaceUrl,
    type: 'GET',
    dataType: 'json'
  });

  // also get style via ajax
  var styleP = $.ajax({
    url: './lib/style.cycss',
    type: 'GET',
    dataType: 'text'
  });

  var styleHc = $.ajax({
    url: './lib/styleHc.cycss',
    type: 'GET',
    dataType: 'text'
  });

  var infoTemplate = Handlebars.compile([
    '<span style="visibility:hidden;">:</span>',
    '<span class="label label-primary" title="state ID">{{id}}</span>',
    '<span class="label label-success" title="type of state">{{type}}</span>',
    '<span class="label label-warning" title="program statement to execute">{{statement}}</span>',
    '{{#each propositions}}<span class="label label-danger" title="assigned atomic proposition">{{ this }}</span> {{/each}}',
  ].join(' '));

  var searchTemplate = Handlebars.compile([
    '<p>ID: {{id}}</p>',
    '<p>Type: {{type}}</p>',
    '<p>Statement: {{statement}}</p>',
    '<p>Propositions:</p>{{#each propositions}}<p>{{ this }}</p>{{/each}}',
  ].join(' '));

  // when both graph export json and style loaded, init cy
  Promise.all([ graphP, styleP ]).then(initCy);

  var allNodes = null;
  var allEles = null;
  var lastHighlighted = null;
  var lastUnhighlighted = null;



  var loadHeap = function( id ) {


        var graphHc = $.ajax({
            url: heapConfUrl + 'hc_' + id + '.json',
            type: 'GET',
            dataType: 'json'
        });

        if(cy2) {
            cy2.destroy();
        }

        Promise.all([ graphHc, styleHc ]).then(initHeap);

  }

  var initHeap = function( then ) {

        var expJson = then[0];
        var styleJson = then[1];
        var elements = expJson.elements;

        cy2 = cytoscape({
            container: document.getElementById('cy2'),
            layout: {
                name: 'dagre',
                padding: hcLayoutPadding,
                rankDir: 'TB',
                nodeSep: 50
            },
            style: styleJson,
            elements: elements,
            selectionType: 'single',
            boxSelectionEnabled: false,
            autoungrabify: false ,
            zoom: 1
        });

        cy2.cxtmenu({
            selector: 'node, edge',
            commands: [
                {
                    content: '<span class="fa fa-expand fa-2x"></span>', // Reset
                    select: function(ele){
                        cy2.stop();
                        cy2.animation({
                            fit: {
                                eles: cy2.elements(),
                                padding: hcLayoutPadding
                            },
                            duration: 0,
                            easing: easing
                        }).play();
                    }
                },{
                    content: '<span class="fa fa-refresh fa-2x"></span>', // Show all',
                    select: function(ele){
                        var allElements = cy2.elements();
                        cy2.elements().forEach(function( e ){
                            e.show();
                        });
                    },
                },{
                    content: '<span class="fa fa-eraser fa-2x"></span>', // Hide',
                    select: function(ele){
                        var e = cy2.getElementById( ele.id() );
                        e.hide();
                    }
                }
            ]
        });

  }

  function getFadePromise( ele, opacity ){
    return ele.animation({
      style: { 'opacity': opacity },
      duration: aniDur
    }).play().promise();
  };



  function highlight( node ){
    var oldNhood = lastHighlighted;

    if(lastHighlightedNodes.length > 0) {
      for(i=0; i < lastHighlightedNodes.length; i++) {
        lastHighlightedNodes[i].position(lastHighlightedNodesPositions[i]);
      }
    }
    lastHighlightedNodes = [];
    lastHighlightedNodesPositions = [];


    var nhood = lastHighlighted = node.closedNeighborhood();


    var others = lastUnhighlighted = cy.elements().not( nhood );

    var reset = function(){
      cy.batch(function(){
        others.addClass('hidden');
        nhood.removeClass('hidden');

        allEles.removeClass('faded highlighted');

        nhood.addClass('highlighted');
      });

      return Promise.resolve().then(function(){
        if( isDirty() ){
          return fit();
        } else {
          return Promise.resolve();
        };
      }).then(function(){
        return Promise.delay( aniDur );
      });
    };

    var runLayout = function(){

      lastHighlightedNodes = nhood.nodes();
      lastHighlightedNodes.forEach(function(n) {
        var pos = Object.assign({}, n.position());
        lastHighlightedNodesPositions.push(pos);
      });

      var l = nhood.filter(':visible').makeLayout({
        name: 'dagre',
        rankDir: 'TB',
        animate: true,
        animationDuration: aniDur,
        animationEasing: easing,
        padding: layoutPadding
      });

      var promise = cy.promiseOn('layoutstop');

      l.run();

      return promise;
    };

    var fit = function(){
      return cy.animation({
        fit: {
          eles: nhood.filter(':visible'),
          padding: layoutPadding
        },
        easing: easing,
        duration: aniDur
      }).play().promise();
    };

    return Promise.resolve()
      .then( reset )
      .then( runLayout )
      .then( fit )
    ;

  }



  function isDirty(){
    return lastHighlighted != null;
  }



  function clear( opts ){
    if( !isDirty() ){ return Promise.resolve(); }

    opts = $.extend({

    }, opts);

    cy.stop();
    allNodes.stop();

    var nhood = lastHighlighted;
    var others = lastUnhighlighted;

    lastHighlighted = lastUnhighlighted = null;

    var hideOthers = function(){
      return Promise.delay( 125 ).then(function(){
        others.addClass('hidden');

        return Promise.delay( 125 );
      });
    };

    var showOthers = function(){
      cy.batch(function(){
        allEles.removeClass('hidden').removeClass('faded');
      });

      return Promise.delay( aniDur );
    };

    var resetHighlight = function(){
      nhood.removeClass('highlighted');
    };

    return Promise.resolve()
      .then( resetHighlight )
      .then( hideOthers )
      .then( showOthers )
    ;
  }



  function showNodeInfo( node ){
    $('#selected-state').html( infoTemplate( node.data() ) ).show();
  }

  function hideNodeInfo(){
    $('#selected-state').html('No state has been selected.').show();
  }


    function resetLayout() {

      if(lastHighlightedNodes.length > 0) {
        for(i=0; i < lastHighlightedNodes.length; i++) {
          lastHighlightedNodes[i].position(lastHighlightedNodesPositions[i]);
        }
      }
      lastHighlightedNodes = [];
      lastHighlightedNodesPositions = [];

      var runLayout = function(){

        var l = cy.elements().filter(':visible').makeLayout({
          name: 'preset'
        });

        var promise = cy.promiseOn('layoutstop');

        l.run();

        return promise;
      };

    return Promise.resolve().then( runLayout );
  }


  function initCy( then ){
    var loading = document.getElementById('loading');
    var expJson = then[0];
    var styleJson = then[1];
    var elements = expJson.elements;

    loading.classList.add('loaded');

    var layout = {
        name: 'breadthfirst',
        directed: true,
        spacingFactor: 1.75,
    };

    if(elements.nodes.length < 1000) {
      layout = {
          name: 'dagre',
          padding: layoutPadding,
          rankDir: 'TB',
      }
    }

    cy = window.cy = cytoscape({
      container: document.getElementById('cy'),
      layout: layout,
      style: styleJson,
      elements: elements,
      motionBlur: false,
      selectionType: 'single',
      boxSelectionEnabled: false,
      autoungrabify: true
    });

    allNodes = cy.nodes();

    allEles = cy.elements();

    resetLayout();


    cy.on('tap', function(){
      $('#search').blur();
    });




    cy.on('select unselect', 'node', _.debounce( function(e){
      var node = cy.$('node:selected');

      if( node.nonempty() ){
        showNodeInfo( node );

        loadHeap( node.data('id') );

        var focusState = $('#focusState').is(':checked');
        if(focusState) {
            Promise.resolve().then(function(){
                return highlight( node );
            });
        }
      } else {
        hideNodeInfo();
        if( isDirty() ){
            clear();
        }
        resetLayout();
      }

    }, 100 ) );


  }

  var lastSearch = '';


  $('#search').typeahead({
    minLength: 1,
    highlight: true,
  },
  {
    name: 'search-dataset',
    source: function( query, cb ){
      function matches( str, q ){
        str = (str || '').toLowerCase();
        q = (q || '').toLowerCase();

        return str.match( q );
      }

      var fields = ['type', 'id', 'statement', 'propositons'];

      function anyFieldMatches( n ){
        for( var i = 0; i < fields.length; i++ ){
          var f = fields[i];

          if( matches( n.data(f), query ) ){
            return true;
          }
        }

        return false;
      }

      function getData(n){
        var data = n.data();

        return data;
      }

      function sortById(n1, n2){
        if( n1.data('id') < n2.data('id') ){
          return -1;
        } else if( n1.data('id') > n2.data('id') ){
          return 1;
        }

        return 0;
      }

      var res = allNodes.stdFilter( anyFieldMatches ).sort( sortById ).map( getData );

      cb( res );
    },
    templates: {
      suggestion: searchTemplate
    }
  }).on('typeahead:selected', function(e, entry, dataset){
    var n = cy.getElementById(entry.id);

    cy.batch(function(){
      allNodes.unselect();

      n.select();
    });

    showNodeInfo( n );
  }).on('keydown keypress keyup change', _.debounce(function(e){
    var thisSearch = $('#search').val();

    if( thisSearch !== lastSearch ){
      $('.tt-dropdown-menu').scrollTop(0);

      lastSearch = thisSearch;
    }
  }, 50));

});
