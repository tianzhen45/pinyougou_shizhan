var app = new Vue({
    el:"#app",
    data: {
        //当前登录用户名
        username:"",
        //购物车列表
        cartList: [],
        //总价格和总数量
        totalValue:{"totalNum":0, "totalMoney":0.0},
        //订单信息
        order:{"paymentType":'1',"invoiceType": "1"},
        //选中的地址
        address: {},
        addressList: [],
        addressEntity: {alias: '',provinceId:'1',cityId:'1',townId:'1'},
        items:[],

        //省份列表
        provinces:[],
        //城市列表
        cities:[],
        //地区列表
        areas:[]

    },
    methods : {
        //提交订单
        submitOrder: function () {
            //设置收件人地址
            this.order.receiverAreaName = this.address.address;
            this.order.receiverMobile = this.address.mobile;
            this.order.receiver = this.address.contact;
            this.order.userId = this.username;

            axios.post("order/add.do", this.order).then(function (response) {
                if(response.data.success){
                    //跳转到支付页面
                    location.href = "pay.html?outTradeNo=" + response.data.message;
                } else {
                    alert(response.data.message);
                }
            });
        },
        //查询当前登录用户的地址列表
        findAddressList: function(){
            axios.get("address/findAddressList.do").then(function (response) {
                app.addressList = response.data;

                for (let i = 0; i < response.data.length; i++) {
                    const address = response.data[i];
                    if (address.isDefault == "1") {
                        app.address = address;
                        break;
                    }
                }
            });
        },

        loadSelectedItem:function () {
            axios.get("/cart/loadSelectedItem.do").then(function (response) {
                app.items = response.data;
                app.totalValue = app.sumTotalValue(response.data);
            })
        },

        // //查询购物车列表
        // findCartList: function(){
        //   axios.get("cart/findCartList.do").then(function (response) {
        //       app.cartList = response.data;
        //
        //       //计算总数量和总价格
        //       app.totalValue = app.sumTotalValue(response.data);
        //   });
        // },
        //计算总数和总价
        sumTotalValue: function (items) {
            var totalValue = {"totalNum":0, "totalMoney":0.0};
            for (let i = 0; i < items.length; i++) {
                const item = items[i];
                totalValue.totalNum += item.num;
                totalValue.totalMoney += item.totalFee;
            }
            return totalValue;
        },
        getUsername: function () {
            axios.get("cart/getUsername.do").then(function (response) {
                app.username = response.data.username;
            });

        },
        //增加地址
        addAddress: function () {



            if(this.addressEntity.id != null){
                this.updateAddress();
                return;
            }

            axios.post("/address/add.do", app.addressEntity).then(function (response) {
                if (response.data) {
                    app.addressEntity = {};
                    app.findAddressList();
                } else {
                    alert("添加地址失败");
                }
            })
        },
        selectAddress: function (address) {
            this.address==address ? this.address = {}  : this.address = address;
        },
        isSelectAddress: function (address) {
            return this.address == address;
        },
        //删除地址
        delAddress: function (address) {
            axios.get("/address/delete.do?ids=" + address.id).then(function (response) {
                if (response.data) {
                    app.findAddressList();
                } else {
                    alert("删除失败");
                }
            });
        },
        //更新地址
        updateAddress: function () {
            axios.post("/address/update.do", app.addressEntity).then(function (response) {
                if (response.data) {
                    app.addressEntity = {};
                    app.findAddressList();
                } else {
                    alert("更新地址失败");
                }
            })
        },
        //设置地址为默认地址
        setDefault:function (address) {
            axios.post("/address/setDefault.do", address).then(function (response) {
                if (response.data) {
                    app.findAddressList();
                } else {
                    alert("设置默认地址失败");
                }
            })
        },
        //加载下拉框
        loadSelect:function () {
            axios.get("/address/findProvinces.do").then(function (response) {
                //加载所有的省份
                app.provinces = response.data;
            });
        },
        findCitiesByProvince:function () {
            axios.get("/address/findCitiesByProvince.do?provinceId="+app.addressEntity.provinceId).then(function (response) {
                app.cities  = response.data;
            });
        },
        findAreasByCity:function () {
            axios.get("/address/findAreasByCity.do?cityId="+app.addressEntity.cityId).then(function (response) {
                app.areas = response.data;
            });
        },
        //点击编辑时，回显省份
        backShow:function (address) {
            app.addressEntity=address;
            axios.get("/address/findCitiesByProvince.do?provinceId="+app.addressEntity.provinceId).then(function (response) {
                app.cities  = response.data;

                app.findAreasByCity();
            });

        }

    },
    created:function () {
        this.getUsername();
        // //加载购物车列表
        // this.findCartList();

        //加载选中的商品
        this.loadSelectedItem();

        //加载地址列表
        this.findAddressList();

        //加载下拉框
        this.loadSelect();
    },
    watch: {
        "addressEntity.provinceId" :function () {
            app.cities = [];
            app.areas = [];

            this.findCitiesByProvince();

        },
        "addressEntity.cityId":function () {
            app.areas = [];

            this.findAreasByCity();
        }
    }
});