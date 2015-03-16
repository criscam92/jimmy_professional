function handleSubmit(args, dialog) {
    var jqDialog = jQuery('#' + dialog);
    if (args.validationFailed) {
        jqDialog.effect('shake', {times: 3}, 100);
    } else {
        PF(dialog).hide();
    }
}

$( document ).ready(function() {
    $("form#FacturaCreateForm div.ui-panel div.ui-panel-titlebar span.ui-panel-title div.ui-selectonebutton div:nth-child(1)").addClass( "ui-state-active" );
    $("form#FacturaCreateForm div.ui-panel div.ui-panel-titlebar span.ui-panel-title div.ui-selectonebutton div:nth-child(2)").removeClass( "ui-state-active" );
});

