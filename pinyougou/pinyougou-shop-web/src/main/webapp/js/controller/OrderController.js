var goodsControllerMixin = {
    el: "#app",
    data: {
        statusList:["未付款","已付款","未发货","已发货","交易成功"],
        //列表数据
        entityList: [],
        entity:{},
        //总记录数
        total: 0,
        //页大小
        pageSize: 5,
        //当前页号
        pageNum: 1,
        //选择的id数组
        ids: [],
        //定一个空的搜索条件对象order
        searchEntity: {},
    },
    methods: {
        //修改为已发货或交易成功
        send(id,status) {
            axios.get("../order/changeStatus.do?orderId="+id+"&status="+status).then(res=>{
                if (!res.data.success) {
                    alert("修改失败");
                }
                this.searchList(1);
            })
        },

        //查询订单列表
        searchList: function (curPage) {
            this.pageNum = curPage;
            axios.post("../order/search.do?pageNum=" + this.pageNum + "&pageSize=" + this.pageSize, this.searchEntity).then(function (response) {
                //this表示axios；所以使用app设置entityList的数据
                app.entityList = response.data.list;
                app.total = response.data.total;
            });
        },
        //根据主键查询
        findOne: function () {
            //获取地址栏的ID参数
            var id = this.getParameterByName('id');
            if(id != null) {
                //说明是修改 则需要根据id查询数据并回显
                axios.get("../goods/findOne/" + id + ".do").then(function (response) {
                    app.entity = response.data;

                    //向富文本编辑器设置商品内容
                    editor.html(app.entity.goodsDesc.introduction);

                    //备份模板ID和扩展属性值
                    app.typeTemplateIdBak=app.entity.goods.typeTemplateId;
                    app.customAttributeItemsBak=JSON.parse( app.entity.goodsDesc.customAttributeItems );

                    //转换商品图片列表
                    app.entity.goodsDesc.itemImages = JSON.parse(app.entity.goodsDesc.itemImages);
                    //转换商品扩展属性
                    app.entity.goodsDesc.customAttributeItems = JSON.parse(app.entity.goodsDesc.customAttributeItems);
                    //转换商品规格属性
                    app.entity.goodsDesc.specificationItems = JSON.parse(app.entity.goodsDesc.specificationItems);
                    //商品SKU列表中的每一个SKU商品的spec转换为json对象
                    if(app.entity.itemList.length > 0){
                        for (var i = 0; i < app.entity.itemList.length; i++) {
                            app.entity.itemList[i].spec = JSON.parse(app.entity.itemList[i].spec);
                        }
                    }
                });
            }
        },
        //根据参数名字获取参数
        getParameterByName: function (name) {
            return decodeURIComponent((new RegExp('[?|&]' + name + '=' + '([^&;]+?)(&|#|;|$)').exec(location.href) || [, ""])[1].replace(/\+/g, '%20')) || null
        }
    },
    created: function () {
        this.searchList(this.pageNum);
    }
};