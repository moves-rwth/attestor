


$(function(){

  var overview = $.ajax({
    url: './data/overview.json',
    type: 'GET',
    dataType: 'json'
  });

  Promise.all([ overview ]).then(init);

  function init( then ){
    var expJson = then[0];
    var elements = expJson.elements;

    var verification = elements.verification;
    var verificationHtml = '';
    for(key in verification) {
      var result = verification[key].result;
      var formula = result.formula;
      var status = result.status;
      if(status == 'valid') {
          verificationHtml += '<a href="statespace.html" class="list-group-item list-group-item-success" title="LTL formula satisfied. Click to show state space.">';
          verificationHtml += formula;
          verificationHtml += '</a>';
      } else if (status == 'invalid') {
        verificationHtml += '<a href="counterexample.html?cex=';
        verificationHtml += key;
        verificationHtml += '" class="list-group-item list-group-item-danger" title="LTL formula violated. Click to show counterexample.">';
        verificationHtml += formula;
        verificationHtml += '</a>';
      } else {
        verificationHtml += '<a href="statespace.html" class="list-group-item list-group-item-warning" title="Could not verify LTL formula. Click to show state space.">';
        verificationHtml += formula;
        verificationHtml += '</a>';
      }
    }
    $('#verificationResults').html( verificationHtml ).show();

    var runtimes = elements.runtimes;
    var runtimesHtml = '';
    for(key in runtimes) {
      var phase = runtimes[key].phase;
      var name = phase.name;
      var time = phase.time;
      runtimesHtml += '<li class="list-group-item d-flex justify-content-between align-items-center">';
      runtimesHtml += name;
      runtimesHtml += '<span class="badge badge-primary badge-pill">';
      runtimesHtml += time;
      runtimesHtml += ' s</span></li>';
    }

    runtimesHtml += '<li class="list-group-item list-group-item-success d-flex justify-content-between align-items-center">';
    runtimesHtml += 'Verification time';
    runtimesHtml += '<span class="badge badge-primary badge-pill">';
    runtimesHtml += elements.verificationTime;
    runtimesHtml += ' s</span></li>';

    runtimesHtml += '<li class="list-group-item active d-flex justify-content-between align-items-center">';
    runtimesHtml += 'Total runtime';
    runtimesHtml += '<span class="badge badge-primary badge-pill">';
    runtimesHtml += elements.totalTime;
    runtimesHtml += ' s</span></li>';

    $('#runtimes').html( runtimesHtml ).show();


  }

});
