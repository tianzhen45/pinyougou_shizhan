var app = new Vue({
    el:"#app",
    data:{
        //搜索条件对象
        searchMap:{"keywords":""},
        //商品列表
        itemList: []
    },
    methods:{
        //搜索
        search:function () {
            axios.post("itemSearch/search.do", this.searchMap).then(function (response) {
                app.itemList = response.data;
            });
        }
    },
    created(){
        //默认查询所有
        this.search();
    }
});