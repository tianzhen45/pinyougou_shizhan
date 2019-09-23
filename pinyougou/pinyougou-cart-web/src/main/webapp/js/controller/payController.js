var app = new Vue({
    el: "#app",
    data: {
        //当前登录用户名
        username: "",
        //交易编号
        outTradeNo: "",
        //支付总金额
        totalFee: 0
    },
    methods: {
        //生成支付二维码
        createNative: function () {
            this.outTradeNo = this.getParameterByName("outTradeNo");
            //发送请求；获取信息
            axios.get("pay/createNative.do?outTradeNo="+this.outTradeNo).then(function (response) {

                if ("SUCCESS"==response.data.result_code) { //设置支付总金额
                    app.totalFee = (response.data.totalFee / 100).toFixed(2);
                    //生成二维码
                    var qr = new QRious({
                        element: document.getElementById("qrious"),
                        level: "M",
                        size: 250,
                        value: response.data.code_url
                    });
                } else {
                    alert("生成支付二维码失败！");
                }
            });

        },
        getUsername: function () {
            axios.get("cart/getUsername.do").then(function (response) {
                app.username = response.data.username;
            });

        },
        //根据参数名字获取参数
        getParameterByName: function (name) {
            return decodeURIComponent((new RegExp('[?|&]' + name + '=' + '([^&;]+?)(&|#|;|$)').exec(location.href) || [, ""])[1].replace(/\+/g, '%20')) || null
        }
    },
    created() {
        this.getUsername();
        //生成二维码
        this.createNative();
    }
});