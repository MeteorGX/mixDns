requirejs.config({
    baseUrl: '/static/js',
    paths: {
        "jquery": "jquery-3.6.1.min",
        "bootstrap": "bootstrap.min",
        "popper": "popper.min",
    },
    shim: {
        "bootstrap":["jquery"]
    }
});
