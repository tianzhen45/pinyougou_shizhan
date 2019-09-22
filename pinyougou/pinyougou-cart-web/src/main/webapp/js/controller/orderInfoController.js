var app = new Vue({
    el:"#app",
    data: {
        //当前登录用户名
        username:"",
        //购物车列表
        cartList: [],
        //总价格和总数量
        totalValue:{"totalNum":0, "totalMoney":0.0},
        //地址列表
        addressList:[],
        //当前选中的地址
        selectedAddress:{},
        //订单信息
        order:{"paymentType":'1'}
    },
    methods : {
        //提交订单
        submitOrder: function () {
            //设置收件人地址
            this.order.receiverAreaName = this.selectedAddress.address;
            this.order.receiverMobile = this.selectedAddress.mobile;
            this.order.receiver = this.selectedAddress.contact;
            axios.post("order/add.do", this.order).then(function (response) {
                if(response.data.success){
                    //跳转到支付页面
                    location.href = "pay.html?outTradeNo=" + response.data.message;
                } else {
                    alert(response.data.message);
                }
            });
        },
        //选择地址
        selectAddress: function (address) {
            this.selectedAddress = address;
        },
        //查询当前登录用户的地址列表
        findAddressList: function(){
            axios.get("address/findAddressList.do").then(function (response) {
                app.addressList = response.data;

                for (let i = 0; i < response.data.length; i++) {
                    const address = response.data[i];
                    if (address.isDefault == "1") {
                        app.selectedAddress = address;
                        break;
                    }
                }
            });
        },

        //查询购物车列表
        findCartList: function(){
          axios.get("cart/findCartList.do").then(function (response) {
              app.cartList = response.data;

              //计算总数量和总价格
              app.totalValue = app.sumTotalValue(response.data);
          });
        },
        //计算总数和总价
        sumTotalValue: function (cartList) {
            var totalValue = {"totalNum":0, "totalMoney":0.0};
            for (let i = 0; i < cartList.length; i++) {
                const cart = cartList[i];
                for (let j = 0; j < cart.orderItemList.length; j++) {
                    const orderItem = cart.orderItemList[j];
                    totalValue.totalNum += orderItem.num;
                    totalValue.totalMoney += orderItem.totalFee;
                }

            }
            return totalValue;
        },
        getUsername: function () {
            axios.get("cart/getUsername.do").then(function (response) {
                app.username = response.data.username;
            });

        }
    },
    created(){
        this.getUsername();
        //加载购物车列表
        this.findCartList();
        //加载地址列表
        this.findAddressList();
    }
});