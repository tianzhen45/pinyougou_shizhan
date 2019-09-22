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
        selectedAddress:{}
    },
    methods : {
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
        //加入购物车
        addItemToCartList: function (itemId, num) {
            axios.get("cart/addItemToCartList.do?itemId=" + itemId + "&num=" + num).then(function (response) {
                if(response.data.success){
                    //刷新购物车列表
                    app.findCartList();
                } else {
                    alert(response.data.message);
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
        //this.findCartList();
        //加载地址列表
        this.findAddressList();
    }
});