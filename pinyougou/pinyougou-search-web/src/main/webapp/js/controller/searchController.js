var app = new Vue({
    el:"#app",
    data:{
        //搜索条件对象
        searchMap:{"keywords":""},
        //返回结果
        resultMap: {"itemList":[]}
    },
    methods:{
        //搜索
        search:function () {
            axios.post("itemSearch/search.do", this.searchMap).then(function (response) {
                app.resultMap = response.data;
            });
        }
    },
    created(){
        //默认查询所有
        this.search();
    }
});