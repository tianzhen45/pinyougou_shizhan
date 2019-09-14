var app = new Vue({
    el:"#app",
    data:{
        //搜索条件对象
        searchMap:{"keywords":"","brand":"","category":"", "spec":{}, "price":""},
        //返回结果
        resultMap: {"itemList":[]}
    },
    methods:{
        //移除过滤条件
        removeSearchItem: function(key){
            if (key == "brand" || key == "category" || key == "price") {
                this.searchMap[key] = "";
            } else {
                //为了同步页面中的显示
                this.$set(this.searchMap.spec, key, null);
                //删除spec对象中某个属性
                delete this.searchMap.spec[key];
            }
            this.search();
        },
        //添加过滤条件对象
        addSearchItem: function(key, value){
            if (key == "brand" || key == "category" || key == "price") {
                this.searchMap[key] = value;
            } else {
                //对于对象中的对象的属性设置的时候；需要使用$set
                //参数1：要设置的对象
                //参数2：设置对象中的属性
                //参数3：设置对象中的属性对应的值
                this.$set(this.searchMap.spec, key, value);
                //this.searchMap.spec[key] = value;
            }
            this.search();
        },
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