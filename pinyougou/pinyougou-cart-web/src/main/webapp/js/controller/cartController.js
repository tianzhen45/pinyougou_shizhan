// 窗口加载完
window.onload = function () {
    var vue = new Vue({
        el: '#app', // 元素绑定
        data: { // 数据模型
            loginName: '',
            carts: [],
            totalEntity: {totalNum: 0, totalMoney: 0.00, totalType: 0},//总计对象
            ids: {sellerIds: [], items: []}, // items是购物车中选中的商品
            checked: false,// 全选复选框是否选中
            delOrderItems: [],//被删除的商品
        },
        methods: { // 操作方法
            //加载用户
            loadUserName: function () {
                //获取登录用户名
                axios.get("/cart/getUsername.do").then(function (response) {
                    vue.loginName = response.data.username;
                })
            },
            //读取购物车列表
            loadCarts: function () {
                axios.get("/cart/findCartList.do").then(function (response) {
                    vue.carts = response.data;

                    //清空总计对象
                    // vue.totalEntity = {totalNum: 0, totalMoney: 0.00, totalType: 0};
                    /* for(var i = 0 ; i < response.data.length ; i ++){
                         var cart = vue.carts[i];
                         //迭代购物车订单列表
                         for(var j = 0 ; j < cart.orderItemList.length ; j++){
                             var orderItem = cart.orderItemList[j];

                             vue.totalEntity.totalNum += orderItem.num;
                             vue.totalEntity.totalMoney += orderItem.totalFee;
                             vue.totalEntity.totalType++;
                         }
                     }*/
                });

            },
            //加入购物车
            addCart: function (itemId, num) {

                axios.get("/cart/addItemToCartList.do?itemId=" + itemId + "&num=" + num).then(function (response) {
                    if (response.data) {
                        //重新加载购物车
                        vue.loadCarts();
                    } else {
                        alert("操作失败！")
                    }
                })
            },
            //加入购物车
            setCart: function (itemId, num) {
                axios.get("/cart/setItemToCartList.do?itemId=" + itemId + "&num=" + num).then(function (response) {
                    if (response.data) {
                        //重新加载购物车
                        vue.loadCarts();
                    } else {
                        alert("操作失败！")
                    }
                })
            },
            checkAll: function (e) { // 全选复选框
                this.ids = {sellerIds: [], items: []}; // 先清空数组
                if (e.target.checked) { // 判断复选框是否选中
                    for (var i = 0; i < this.carts.length; i++) {
                        var cart = this.carts[i];
                        this.ids.sellerIds.push(cart.sellerId);
                        for (var j = 0; j < cart.orderItemList.length; j++) {
                            var orderItem = cart.orderItemList[j];
                            this.ids.items.push(orderItem);
                        }
                    }
                }
            },
            checkSeller: function (e, sellerId) {
                //把该商家下的所有商品选中或取消
                var cart = this.findCartBySellerId(sellerId);
                if (e.target.checked) { // 判断复选框是否选中
                    for (var i = 0; i < cart.orderItemList.length; i++) {
                        var orderItem = cart.orderItemList[i];
                        if (this.ids.items.indexOf(orderItem) == -1) {
                            this.ids.items.push(orderItem);
                        }
                    }
                } else {
                    for (var i = 0; i < cart.orderItemList.length; i++) {
                        var orderItem = cart.orderItemList[i];
                        var index = this.ids.items.indexOf(orderItem);
                        if (index != -1) {
                            this.ids.items.splice(index, 1);
                        }
                    }
                }
            },
            checkItem: function (e, sellerId) {

                if (!e.target.checked) {
                    var index = this.ids.sellerIds.indexOf(sellerId);
                    if (index != -1) {
                        this.ids.sellerIds.splice(index, 1);
                    }
                }
            }
            ,
            //根据商家id找到商家购物车
            findCartBySellerId: function (sellerId) {
                for (var i = 0; i < this.carts.length; i++) {
                    var cart = this.carts[i];
                    if (cart.sellerId == sellerId) {
                        return cart;
                    }
                }
            },
            //根据商品id找到orderItem
            findItemByItemId: function (itemId) {
                for (var i = 0; i < this.carts.length; i++) {
                    var cart = this.carts[i];
                    this.ids.sellerIds.push(cart.sellerId);
                    for (var j = 0; j < cart.orderItemList.length; j++) {
                        var orderItem = cart.orderItemList[j];
                        if (orderItem.itemId = itemId) {
                            return orderItem;
                        }
                    }
                }
            },
            delItem: function (itemId) {

                //将要删除的商品加入delOrderItems中
                var orderItem = this.findItemByItemId(itemId);
                this.delOrderItems.push(orderItem);

                //将ids.items置空
                this.ids.items = [];

                //删除商品
                this.setCart(itemId, 0);
            },
            //批量删除
            delBatch: function () {
                if (!confirm("确定要删除选中的多个商品吗？")) {
                    return;
                }

                //删除之前把商品加入delOrderItems中
                var items = this.ids.items;
                for (var i = 0; i < items.length; i++) {
                    var item = items[i];
                    this.delOrderItems.push(item);
                }


                axios.post("/cart/delBatch.do",vue.ids.items).then(function (response) {
                    if (response.data) {
                        vue.loadCarts();

                        //将ids.items置空
                        vue.ids.items = [];
                    } else {
                        alert("操作失败");
                    }
                })
            },
            //重新购买
            reBuy: function (itemId, num) {
                this.addCart(itemId, num);
                //删除 删除列表中的这条
                for (var i = 0; i < this.delOrderItems.length; i++) {
                    var orderItem = this.delOrderItems[i];
                    if (orderItem.itemId == itemId) {
                        this.delOrderItems.splice(i, 1);
                    }
                }
                //清空选中
                this.ids =  {sellerIds: [], items: []};
            },

            //刷新总价格
            refreshPrice: function () {

                //清空总计对象
                this.totalEntity = {totalNum: 0, totalMoney: 0.00, totalType: 0};

                var items = this.ids.items;
                for (var i = 0; i < items.length; i++) {
                    var item = items[i];
                    this.totalEntity.totalMoney += item.totalFee;
                    this.totalEntity.totalType++;
                    this.totalEntity.totalNum += item.num;
                }
            },
            //保存选中的购物车商品到redis中
            saveSelectedItem:function () {
                axios.post("/cart/saveSelectedItem.do",vue.ids.items).then(function (response) {

                    location.href = "/getOrderInfo.html";
                });
            }
        },
        created: function () { // 创建生命周期
            this.loadUserName();
            this.loadCarts();

        },
        updated: function () { // 更新数据生命周期
            // 检查全选checkbox是否选中
            this.checked = (this.ids.sellerIds.length == this.carts.length && this.ids.items.length == this.totalEntity.totalType);

        },
        //监听选中的商品变化，更新总价格数据
        watch: {
            "ids.items":function () {
                this.$nextTick(function(){
                    vue.refreshPrice();
                });
            }
        }
    });
};