package com.xiaoe.shop.wxb.business.comment.presenter;

import android.text.TextUtils;

import com.xiaoe.common.entitys.CommentEntity;
import com.xiaoe.network.network_interface.IBizCallback;
import com.xiaoe.network.network_interface.INetworkResponse;
import com.xiaoe.network.requests.CommentDeleteRequest;
import com.xiaoe.network.requests.CommentLikeRequest;
import com.xiaoe.network.requests.CommentListRequest;
import com.xiaoe.network.requests.IRequest;
import com.xiaoe.network.requests.SendCommentRequest;

public class CommentPresenter implements IBizCallback {
    private static final String TAG = "CommentPresenter";
    private INetworkResponse iNetworkResponse;

    public CommentPresenter(INetworkResponse iNetworkResponse) {
        this.iNetworkResponse = iNetworkResponse;
    }

    @Override
    public void onResponse(final IRequest iRequest, final boolean success, final Object entity) {
        iNetworkResponse.onResponse(iRequest, success, entity);
    }

    /**
     * 获取评论列表
     * @param recordId
     * @param recordType 	1-图文 2-音频 3-视频，20-电子书
     * @param pageSize
     * @param lastCommentId
     * @param label last-最新,hot-热门，默认是last
     */
    public void requestCommentList(String recordId, int recordType, int pageSize, int lastCommentId, String label){
        CommentListRequest listRequest = new CommentListRequest(this);
        listRequest.addDataParam("record_id",recordId);
        listRequest.addDataParam("record_type",recordType);
        listRequest.addDataParam("page_size",pageSize);
        listRequest.addDataParam("last_comment_id",lastCommentId);
        if (TextUtils.isEmpty(label)) {
            label = "last";
        }
        listRequest.addDataParam("label",label);
        listRequest.sendRequest();
    }

    /**
     * 发送评论
     * @param recordId
     * @param recordType
     * @param recordTitle
     * @param content
     * @param replyComment 被回复的信息
     */
    public void sendComment(String recordId, int recordType, String recordTitle, String content, CommentEntity replyComment){
        SendCommentRequest commentRequest = new SendCommentRequest(this);
        commentRequest.addDataParam("record_id",recordId);
        commentRequest.addDataParam("record_type",recordType);
        commentRequest.addDataParam("record_title",recordTitle);
        commentRequest.addDataParam("agent_type ",2);
        if(replyComment != null){
            commentRequest.addDataParam("src_comment_id",replyComment.getComment_id());
            commentRequest.addDataParam("src_content",replyComment.getContent());
            commentRequest.addDataParam("src_nick_name",replyComment.getUser_nickname());
            commentRequest.addDataParam("src_user_id",replyComment.getUser_id());
        }
        commentRequest.addDataParam("content",content);

        commentRequest.sendRequest();
    }

    /**
     * 评论点赞
     * @param recordId
     * @param recordType
     * @param commentId
     * @param srcUserId
     * @param commentContent
     * @param praised
     */
    public void likeComment(String recordId, int recordType, int commentId, String srcUserId, String commentContent, boolean praised){
        CommentLikeRequest commentLikeRequest = new CommentLikeRequest(this);
        commentLikeRequest.addDataParam("record_id",recordId);
        commentLikeRequest.addDataParam("record_type",recordType);
        commentLikeRequest.addDataParam("comment_id",commentId);
        commentLikeRequest.addDataParam("src_user_id",srcUserId);
        commentLikeRequest.addDataParam("src_comment_content",commentContent);
        commentLikeRequest.addDataParam("is_praised",praised);
        commentLikeRequest.sendRequest();
    }
    public void deleteComment(String recordId, int recordType, int commentId){
        CommentDeleteRequest commentDeleteRequest = new CommentDeleteRequest(this);
        commentDeleteRequest.addDataParam("comment_id",commentId);
        commentDeleteRequest.addDataParam("record_id",recordId);
        commentDeleteRequest.addDataParam("record_type",recordType);

        commentDeleteRequest.sendRequest();
    }
}
