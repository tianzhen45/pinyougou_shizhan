
    var app = new Vue({
        el: '#account', // 元素绑定
        data: { // 数据模型
            entity:{},
            addressList: [], // 地址列表
            // address
            address: {alias: '', provinceId: '', cityId: '', townId: ''}, // 添加地址
            AliasAddressArr: ["家里", "父母家", "公司"],
            provinceList: [], // 省地址列表
            areaList: [], // 地区地址列表
            cityList: [], // 城市地址列表
            provinceId: '', // 省份id
            cityId: '', // 城市id
            townId: '', // 地区id
            province: '', // 省份名称
            city: '', // 城市名称
            townid: '', // 地区名称
            updateAddress: {}, // 修改地址保存
            urlPic : '',
        },
        methods: { // 操作方法
            getUserInfo: function () {
                axios.get("user/getUserInfo.do").then(function (response) {
                    app.entity = response.data.user;
                });
            },

            // 显示用户地址列表
            //查询当前登录用户的地址列表
            findAddressList: function(){
                axios.get("address/findAddressList.do").then(function (response) {
                    app.addressList = response.data;
                });
            },
            // 修改用户地址状态
            updateStatus(id) {
                axios.get("address/updateStatus.do?id=" + id).then(response => {
                    // 判断返回结果
                    // 重新查询地址列表
                    alert("设置成功");
                    this.findAddressList();
                });
            },
            // 选择常用别名
            selectAliasAddress(index) {
                this.address.alias = this.AliasAddressArr[index];
            },
            // 查询省地址列表
            findProvinceList: function () {
                this.provinceList = []
                axios.get("address/findProvinceList.do").then(function (response) {
                    app.provinceList = response.data;
                });
            },
            // 根据父级id查询子地区列表
            findCityListByProvinceId:function (provinceId) {
                axios.get("address/findCityListByProvinceId.do?provinceId=" + provinceId).then(function (response) {
                    app.cityList = response.data;
                })
            },
            findAreaListByCityId:function (cityId) {
                axios.get("address/findAreaListByCityId.do?cityId=" + cityId).then(function (response) {
                    app.areaList = response.data;
                })
            },
            // 编辑回显数据
            show(address) {
                var addressStr = JSON.stringify(address);
                this.address = JSON.parse(addressStr);
            },
            // 添加或修改地址
            saveOrUpdate() {
                //  alert(this.address.id);
                // 判断主键id是否存在
                var url = "save";
                if (this.address.id) {
                    url = "update";
                }

                // 发送异步请求
                axios.post("/address/" + url+".do", this.address).then(response => {
                    // 接收返回数据
                    if (url == "update" && response.data) {
                        alert("修改成功");
                    } else if (url == "save" && response.data) {
                        alert("添加成功");
                    } else {
                        alert("操作失败");
                    }
                    // 重新查询用户地址列表
                    this.findAddressList();
                });
            },

            // 删除用户地址
            del(id, status) {
                //alert(id + ":" + status);
                // 判断是否为默认状态，默认则不能删除
                if (status == 1) {
                    alert("默认地址不能删除！");
                } else {
                    if (confirm("你确定要删除地址吗？")) {
                        // 发送异步请求
                        axios.get("/address/deleteAddress.do?id=" + id).then(response => {
                            // 判断返回结果
                            if (response.data) {
                                alert("删除成功");
                                // 重新查询用户地址列表
                                this.findAddressList();
                            } else {
                                alert("删除失败！");
                            }
                        });
                    }
                }
            },
        },
        // 监听省份地址id
        watch: {
            //监听1级分类值的变化，获取2级分类
            "address.provinceId": function (newValue, oldValue) {
                //加载2级分类
                this.findCityListByProvinceId(newValue, "cityList");
                //清空3级分类
                this.areaList = [];

            },
            //监听2级分类值的变化，获取3级分类
            "address.cityId": function (newValue, oldValue) {
                //加载3级分类
                this.findAreaListByCityId(newValue, "areaList");
            },
        },
        // 初始化生命周期
        created: function () {
            this.getUserInfo(); // 初始化加载登录用户名
            this.findAddressList(); // 初始化加载用户地址列表
            this.findProvinceList(); // 初始化省份列表
        },
    });
