jQuery(function() {
    var $ = jQuery,    // just in case. Make sure it's not an other libaray.

    $wrap = $('#pictureUploader'),

    $queue = $('<ul class="filelist"></ul>')
        .appendTo( $wrap.find('.queueList') ),

    // WebUploader实例
    uploader;

    // 实例化
    uploader = WebUploader.create({
        pick: {
            id: '#pictureFilePicker',
            label: '点击选择图片'
        },
        paste: document.body,

        accept: {
            title: 'Images',
            extensions: 'gif,jpg,jpeg,bmp,png',
            mimeTypes: 'image/*'
        },

        // swf文件路径
        swf: BASE_URL + '/Uploader.swf',

        server: '/data/doPictureUpload',
        fileNumLimit: 1,
        fileSizeLimit: 1024 * 1024,    //1M
        fileSingleSizeLimit: 1024 * 1024,    // 1M
        resize: false
    });

    // 当有文件添加进来时执行，负责view的创建
    function addFile( file ) {
        $('#pictureFilePicker').hide();
        var $li = $( '<li id="' + file.id + '">' +
                '<p class="imgWrap"></p>'+
                '</li>' ),
            $btns = $('<div class="file-panel">' +
                '<span class="cancel btn btn-danger">删除</span> ' +
                '<span class="start btn btn-success">上传</span></div>').appendTo( $li ),
            $wrap = $li.find( 'p.imgWrap' ),
            $info = $('<p class="error"></p>');


        showError = function( code ) {
            switch( code ) {
                case 'exceed_size':
                    text = '文件大小超出';
                    break;

                case 'interrupt':
                    text = '上传暂停';
                    break;

                default:
                    text = '上传失败，请重试';
                    break;
            }
            $info.text( text ).appendTo( $li );
        };

        if ( file.getStatus() === 'invalid' ) {
            showError( file.statusText );
        }
        $btns.on( 'click', 'span', function() {
            var index = $(this).index(),
                deg;

            switch ( index ) {
                case 0:
                    uploader.removeFile( file );
                    return;
                case 1:
                    uploader.upload();
                    return ;
            }
        });

        uploader.makeThumb( file, function( error, src ) {
            if ( error ) {
                $wrap.text( '不能预览' );
                return;
            }
            var img = $('<img src="'+src+'">');
            $wrap.empty().append( img );
        }, 110, 110 );

        $li.appendTo( $queue );
    }

    // 负责view的销毁
    function removeFile( file ) {
        $('#pictureFilePicker').show();
        var $li = $('#'+file.id);

        $li.off().find('.file-panel').off().end().remove();
    }

    uploader.onUploadAccept = function( object, ret ) {
        var $li = $( '#'+object.file.id );
        var $filePanel = $li.find('.file-panel');
        $filePanel.hide();

        if(typeof ret == 'object' && ret ){
            //是JSON
            var $pictureUrl = $('#picture');
            $pictureUrl.val(ret.picture);
        }else{
            //不是JSON
            var $info5 = $('<p class="success"></p>');
            $info5.text("服务器出错").appendTo( $li );
        }
    };

    uploader.onUploadProgress = function( file, percentage ) {
    };

    uploader.onFileQueued = function( file ) {
        addFile( file );
    };

    uploader.onFileDequeued = function( file ) {
        removeFile( file );
    };

    uploader.onError = function( code ) {
        alert( 'Eroor: ' + code );
    };

    uploader.on( 'uploadSuccess', function( file ) {
        var $li = $( '#'+file.id );
        var $info3 = $('<p class="success"></p>');
        $info3.text("上传成功").appendTo( $li );
    });

    uploader.on( 'uploadError', function( file ) {
        var $li = $( '#'+file.id );
        var $info4 = $('<p class="error"></p>');
        $info4.text("上传失败").appendTo( $li );
    });

    uploader.on( 'uploadComplete', function( file ) {
        //alert("上传完成");
    });
});
