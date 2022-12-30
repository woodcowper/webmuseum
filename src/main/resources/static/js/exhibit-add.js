$( document ).ready(function(){
    hiddenUncessaryBlock();
    $("#delete-img").click(function(){
        $("#img-block").hide();
        $("#select-img-block").show();
        $("#imgFileName").val("");
    });

    $("#categories").select2();

    $("#btn-add-author").click(addNewAuthor);

    loadFieldsData();
});

const authorBlockTemplate = `<div class="mb-3 img-thumbnail" data-idx="|n|" id="author-block-|n|">
            <input name="authors[|n|].exhibitId" type="hidden" id="exhibit-id-|n|">
            <label class="form-label">Author</label>
            <select name="authors[|n|].authorId" class="w-100 mb-3 author-list-author-select">
                <option value="">None</option>
            </select>
            <label class="form-label">Collection</label>
            <select name="authors[|n|].collectionId" class="w-100 mb-3 collection-select">
                <option value="">None</option>
            </select>

            <button type="button" id="delete-author-|n|" class="mt-3 btn btn-outline-danger btn-delete-author">Delete author</button>

          </div>`;

function addNewAuthor(){
    var idx = getNewAuthorBlockIdx();
    var newBlock = authorBlockTemplate.replaceAll("|n|", idx);
    $("#author-list").append(newBlock);
    $("#exhibit-id-" + idx).val($("#id").val());
    $("#delete-author-" + idx).click(deleteAuthor);
    var block = $("#author-block-" + idx);
    $(block).find(".author-list-author-select").select2({
        data: getAuthorList()
    });
    //author-select
    $(block).find(".author-list-author-select").change(authorSelectOnChange);
    //collection-select
    $(block).find(".collection-select").select2();
}

function loadFieldsData(){
    $(".btn-delete-author").click(deleteAuthor);
    $(".author-list-author-select").change(authorSelectOnChange);
    var authorBlocks = $("#author-list > div");
    $("#author-list > div").each(function(idx, el){
        var curBlock = $(el);
        var curAuthorSelect = curBlock.find(".author-list-author-select");
        var curCollectionSelect = curBlock.find(".collection-select");
        var curAuthorId = curAuthorSelect.data("start-val");
        curAuthorId = curAuthorId == "" ? null : curAuthorId;
        curAuthorSelect.select2({
            data: getAuthorList(curAuthorId)
        });
        if(curAuthorId != null){
            curAuthorSelect.val(curAuthorId);
            curAuthorSelect.change();
            curAuthorSelect.data("start-val", "");
        }    
    });
}

function authorSelectOnChange(e){
    var val = e.target.value;
    if(val != ""){
        var collectionSelect = $(e.target).parent().find(".collection-select");
        ajaxGetCollections(val, collectionSelect);
    }
}

function ajaxGetCollections(val, select){
    $.ajax({
        url: "/ajax/get-collections",
        data: {
            authorId: val
        },
        type: "GET",

        success: function(data){
            data = JSON.parse(data);
            var selectData = data.map(function(el){
                return {id: el.id, text: el.name};
            });
            var curCollectionId = select.data("start-val");
            curCollectionId = curCollectionId == "" ? null : curCollectionId;
            select.html(`<option value="">None</option>`);
            select.select2({
                data: selectData
            });
            if(curCollectionId != null){
                select.val(curCollectionId);
                select.change();
                select.data("start-val", "");
            }
        }
    });
}

function deleteAuthor(e){
    var blockId = $(e.target).parent().attr("id");
    $("#" + blockId).remove();
}

function getNewAuthorBlockIdx(){
    var authorBlocks = $("#author-list > div");
    if(authorBlocks.length == 0){
        return 0;
    }
    var ids = [];
    authorBlocks.each(function(idx, el){
        ids.push($(el).data("idx"));
    });
    return Math.max.apply(Math, ids) + 1;
}

function getAuthorList(){
    return getAuthorList(null);
}

function getAuthorList(curVal){
    var authorsIds = [];
    var authorBlocks = $("#author-list > div");
    authorBlocks.each(function(idx, el){
        var val = $(el).find(".author-list-author-select").val();
        if(val == ""){
            val = $(el).find(".author-list-author-select").data("start-val");
        }
        if(val && val != curVal){
            authorsIds.push(val + "");
        }
    });
    return authors.filter(e => !authorsIds.includes(e.id + ""))
                                  .map(function(el){ return {id: el.id, text: el.fullName}; });
}


function hiddenUncessaryBlock(){
    var imgSrc = $("#img-tag").attr("src");
    if(imgSrc == null || imgSrc == ""){
        $("#img-block").hide();
        $("#select-img-block").show();
    } else {
        $("#img-block").show();
        $("#select-img-block").hide();
    }
}