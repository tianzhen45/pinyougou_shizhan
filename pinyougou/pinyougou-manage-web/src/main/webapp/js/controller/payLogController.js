var app = new Vue({
    el : "#app",
    data : {
        // 列表数据
        entityList: [],
        // 总记录数
        total: 0,
        // 页大小
        pageSize: 10,
        // 当前页号
        pageNum: 1,
        // 实体
        entity: {},
        // 选择的id数组
        outTradeNos: [],
        // 定一个空的搜索条件对象
        searchEntity: {},
        // 支付状态
        tradeState: { 0: "未支付", 1: "已支付"},
        // 全选的复选框
        checkAll: false

    },
    methods : {
        //选择全部
        selectAll: function () {
            if (!this.checkAll) {
                this.outTradeNos = [];
                //选中
                for (let i = 0; i < this.entityList.length; i++) {
                    var entity = this.entityList[i];
                    this.outTradeNos.push(entity.outTradeNo);
                }
            } else {
                //将所有的选择反选
                this.outTradeNos = [];
            }
        },
        // 查询
        searchList : function (curPage) {
            this.pageNum = curPage;
            axios.post("../payLog/search.do?pageNum=" + this.pageNum + "&pageSize=" + this.pageSize, this.searchEntity).then(function (response) {
                //this表示axios；所以使用app设置entityList的数据
                app.entityList = response.data.list;
                app.total = response.data.total;
            });
        },
        // 导出支付日志
        exportExcel : function(){

            if (this.outTradeNos.length == 0) {
                alert("请先选择要导出的记录！");
                return;
            }
            //confirm 如果点击 确定 则返回true
            if(confirm("确定要导出选中了的那些记录吗？")){
                axios.post("../payLog/export.do", this.outTradeNos.entries).then(function (response) {
                    if(response.data.success){
                        alert("导出成功！")
                    } else {
                        alert("导出失败！");
                    }
                });
            }
        }

    },
    created : function () {
        this.searchList(this.pageNum);
    }
});