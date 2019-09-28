var goodsControllerMixin = {
    el: "#app",
    data: {
        statusList:["未支付","审核中","审核通过","审核不通过"],
        //列表数据 (秒杀订单 + 秒杀商品)
        entityList: [],
        entity:{},
        //总记录数
        total: 0,
        //页大小
        pageSize: 2,
        //当前页号
        pageNum: 1,
        //选择的id数组
        ids: [],
        //定一个空的搜索条件对象order
        searchEntity: {},
        resultMap:{"seckillOrderList":[]},
        checkAll:false,


    },
    //监控数据属性的变化
    watch:{
        ids:{
            //开启深度监控；可以监控里面具体元素的改变
            deep: true,
            //处理方法
            handler: function (newValue, oldValue) {
                if (this.ids.length == this.resultMap.seckillOrderList.length && this.resultMap.seckillOrderList.length>0) {
                    this.checkAll = true;
                } else {
                    this.checkAll = false;
                }
            }
        }
    },
    methods: {

        //批量删除
        deleteSeckillOrder: function () {
            if (this.ids.length == 0) {
                alert("请先选择要删除的记录！");
                return;
            }
            //确认是否要删除
            //confirm 如果点击 确定 则返回true
            if(confirm("确定要删除选中了的那些记录吗？")){
                axios.get("../seckillOrder/deleteSeckillOrder.do?ids=" + this.ids).then(function (response) {
                    if(response.data.success){
                        app.queryByPageNo(1);
                        //清空选择的id
                        app.ids = [];
                        app.searchList(1);
                    } else {
                        alert(response.data.message);
                    }
                });
            }
        },
        //选择全部
        selectAll: function () {
            if (!this.checkAll) {
                this.ids = [];
                //选中
                for (let i = 0; i < this.resultMap.seckillOrderList.length; i++) {
                    var vo = this.resultMap.seckillOrderList[i];
                    this.ids.push(vo.seckillOrder.id);
                }
            } else {
                //将所有的选择反选
                this.ids = [];
            }
        },


        //查询订单列表
        searchList: function (curPage) {
            this.pageNum = curPage;
            axios.post("../seckillOrder/search.do?pageNum=" + this.pageNum + "&pageSize=" + this.pageSize, this.searchEntity).then(function (response) {
                //this表示axios；所以使用app设置entityList的数据
                app.entityList = response.data.seckillOrderList;
                app.resultMap.seckillOrderList = response.data.seckillOrderList;
                app.total = response.data.total;
                app.pageNum = response.data.pageNum;
                app.ids=[];
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