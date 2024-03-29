// **********************************************************************
//
// Copyright (c) 2003-2010 ZeroC, Inc. All rights reserved.
//
// This copy of Ice is licensed to you under the terms described in the
// ICE_LICENSE file included in this distribution.
//
// **********************************************************************

// Ice version 3.4.0

package cn.joy.ggg.api.service;

// <auto-generated>
//
// Generated from file `comment.ice'
//
// Warning: do not edit this file.
//
// </auto-generated>


public final class CommentServicePrxHelper extends Ice.ObjectPrxHelperBase implements CommentServicePrx
{
    public cn.joy.ggg.api.model.CommentResponse
    getCommentList(String appId, String cid, int start, int limit)
    {
        return getCommentList(appId, cid, start, limit, null, false);
    }

    public cn.joy.ggg.api.model.CommentResponse
    getCommentList(String appId, String cid, int start, int limit, java.util.Map<String, String> __ctx)
    {
        return getCommentList(appId, cid, start, limit, __ctx, true);
    }

    private cn.joy.ggg.api.model.CommentResponse
    getCommentList(String appId, String cid, int start, int limit, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
        {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while(true)
        {
            Ice._ObjectDel __delBase = null;
            try
            {
                __checkTwowayOnly("getCommentList");
                __delBase = __getDelegate(false);
                _CommentServiceDel __del = (_CommentServiceDel)__delBase;
                return __del.getCommentList(appId, cid, start, limit, __ctx);
            }
            catch(IceInternal.LocalExceptionWrapper __ex)
            {
                __handleExceptionWrapper(__delBase, __ex);
            }
            catch(Ice.LocalException __ex)
            {
                __cnt = __handleException(__delBase, __ex, null, __cnt);
            }
        }
    }

    private static final String __getCommentList_name = "getCommentList";

    public Ice.AsyncResult begin_getCommentList(String appId, String cid, int start, int limit)
    {
        return begin_getCommentList(appId, cid, start, limit, null, false, null);
    }

    public Ice.AsyncResult begin_getCommentList(String appId, String cid, int start, int limit, java.util.Map<String, String> __ctx)
    {
        return begin_getCommentList(appId, cid, start, limit, __ctx, true, null);
    }

    public Ice.AsyncResult begin_getCommentList(String appId, String cid, int start, int limit, Ice.Callback __cb)
    {
        return begin_getCommentList(appId, cid, start, limit, null, false, __cb);
    }

    public Ice.AsyncResult begin_getCommentList(String appId, String cid, int start, int limit, java.util.Map<String, String> __ctx, Ice.Callback __cb)
    {
        return begin_getCommentList(appId, cid, start, limit, __ctx, true, __cb);
    }

    public Ice.AsyncResult begin_getCommentList(String appId, String cid, int start, int limit, Callback_CommentService_getCommentList __cb)
    {
        return begin_getCommentList(appId, cid, start, limit, null, false, __cb);
    }

    public Ice.AsyncResult begin_getCommentList(String appId, String cid, int start, int limit, java.util.Map<String, String> __ctx, Callback_CommentService_getCommentList __cb)
    {
        return begin_getCommentList(appId, cid, start, limit, __ctx, true, __cb);
    }

    private Ice.AsyncResult begin_getCommentList(String appId, String cid, int start, int limit, java.util.Map<String, String> __ctx, boolean __explicitCtx, IceInternal.CallbackBase __cb)
    {
        __checkAsyncTwowayOnly(__getCommentList_name);
        IceInternal.OutgoingAsync __result = new IceInternal.OutgoingAsync(this, __getCommentList_name, __cb);
        try
        {
            __result.__prepare(__getCommentList_name, Ice.OperationMode.Normal, __ctx, __explicitCtx);
            IceInternal.BasicStream __os = __result.__os();
            __os.writeString(appId);
            __os.writeString(cid);
            __os.writeInt(start);
            __os.writeInt(limit);
            __os.endWriteEncaps();
            __result.__send(true);
        }
        catch(Ice.LocalException __ex)
        {
            __result.__exceptionAsync(__ex);
        }
        return __result;
    }

    public cn.joy.ggg.api.model.CommentResponse end_getCommentList(Ice.AsyncResult __result)
    {
        Ice.AsyncResult.__check(__result, this, __getCommentList_name);
        if(!__result.__wait())
        {
            try
            {
                __result.__throwUserException();
            }
            catch(Ice.UserException __ex)
            {
                throw new Ice.UnknownUserException(__ex.ice_name());
            }
        }
        cn.joy.ggg.api.model.CommentResponse __ret;
        IceInternal.BasicStream __is = __result.__is();
        __is.startReadEncaps();
        __ret = new cn.joy.ggg.api.model.CommentResponse();
        __ret.__read(__is);
        __is.endReadEncaps();
        return __ret;
    }

    public cn.joy.ggg.api.model.CommentResponse
    postComment(String appId, String userId, String cid, String connect, boolean isAnonymity)
    {
        return postComment(appId, userId, cid, connect, isAnonymity, null, false);
    }

    public cn.joy.ggg.api.model.CommentResponse
    postComment(String appId, String userId, String cid, String connect, boolean isAnonymity, java.util.Map<String, String> __ctx)
    {
        return postComment(appId, userId, cid, connect, isAnonymity, __ctx, true);
    }

    private cn.joy.ggg.api.model.CommentResponse
    postComment(String appId, String userId, String cid, String connect, boolean isAnonymity, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
        {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while(true)
        {
            Ice._ObjectDel __delBase = null;
            try
            {
                __checkTwowayOnly("postComment");
                __delBase = __getDelegate(false);
                _CommentServiceDel __del = (_CommentServiceDel)__delBase;
                return __del.postComment(appId, userId, cid, connect, isAnonymity, __ctx);
            }
            catch(IceInternal.LocalExceptionWrapper __ex)
            {
                __handleExceptionWrapper(__delBase, __ex);
            }
            catch(Ice.LocalException __ex)
            {
                __cnt = __handleException(__delBase, __ex, null, __cnt);
            }
        }
    }

    private static final String __postComment_name = "postComment";

    public Ice.AsyncResult begin_postComment(String appId, String userId, String cid, String connect, boolean isAnonymity)
    {
        return begin_postComment(appId, userId, cid, connect, isAnonymity, null, false, null);
    }

    public Ice.AsyncResult begin_postComment(String appId, String userId, String cid, String connect, boolean isAnonymity, java.util.Map<String, String> __ctx)
    {
        return begin_postComment(appId, userId, cid, connect, isAnonymity, __ctx, true, null);
    }

    public Ice.AsyncResult begin_postComment(String appId, String userId, String cid, String connect, boolean isAnonymity, Ice.Callback __cb)
    {
        return begin_postComment(appId, userId, cid, connect, isAnonymity, null, false, __cb);
    }

    public Ice.AsyncResult begin_postComment(String appId, String userId, String cid, String connect, boolean isAnonymity, java.util.Map<String, String> __ctx, Ice.Callback __cb)
    {
        return begin_postComment(appId, userId, cid, connect, isAnonymity, __ctx, true, __cb);
    }

    public Ice.AsyncResult begin_postComment(String appId, String userId, String cid, String connect, boolean isAnonymity, Callback_CommentService_postComment __cb)
    {
        return begin_postComment(appId, userId, cid, connect, isAnonymity, null, false, __cb);
    }

    public Ice.AsyncResult begin_postComment(String appId, String userId, String cid, String connect, boolean isAnonymity, java.util.Map<String, String> __ctx, Callback_CommentService_postComment __cb)
    {
        return begin_postComment(appId, userId, cid, connect, isAnonymity, __ctx, true, __cb);
    }

    private Ice.AsyncResult begin_postComment(String appId, String userId, String cid, String connect, boolean isAnonymity, java.util.Map<String, String> __ctx, boolean __explicitCtx, IceInternal.CallbackBase __cb)
    {
        __checkAsyncTwowayOnly(__postComment_name);
        IceInternal.OutgoingAsync __result = new IceInternal.OutgoingAsync(this, __postComment_name, __cb);
        try
        {
            __result.__prepare(__postComment_name, Ice.OperationMode.Normal, __ctx, __explicitCtx);
            IceInternal.BasicStream __os = __result.__os();
            __os.writeString(appId);
            __os.writeString(userId);
            __os.writeString(cid);
            __os.writeString(connect);
            __os.writeBool(isAnonymity);
            __os.endWriteEncaps();
            __result.__send(true);
        }
        catch(Ice.LocalException __ex)
        {
            __result.__exceptionAsync(__ex);
        }
        return __result;
    }

    public cn.joy.ggg.api.model.CommentResponse end_postComment(Ice.AsyncResult __result)
    {
        Ice.AsyncResult.__check(__result, this, __postComment_name);
        if(!__result.__wait())
        {
            try
            {
                __result.__throwUserException();
            }
            catch(Ice.UserException __ex)
            {
                throw new Ice.UnknownUserException(__ex.ice_name());
            }
        }
        cn.joy.ggg.api.model.CommentResponse __ret;
        IceInternal.BasicStream __is = __result.__is();
        __is.startReadEncaps();
        __ret = new cn.joy.ggg.api.model.CommentResponse();
        __ret.__read(__is);
        __is.endReadEncaps();
        return __ret;
    }

    public cn.joy.ggg.api.model.CommentResponse
    removeComment(String appId, String cid, String commentIds)
    {
        return removeComment(appId, cid, commentIds, null, false);
    }

    public cn.joy.ggg.api.model.CommentResponse
    removeComment(String appId, String cid, String commentIds, java.util.Map<String, String> __ctx)
    {
        return removeComment(appId, cid, commentIds, __ctx, true);
    }

    private cn.joy.ggg.api.model.CommentResponse
    removeComment(String appId, String cid, String commentIds, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
        {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while(true)
        {
            Ice._ObjectDel __delBase = null;
            try
            {
                __checkTwowayOnly("removeComment");
                __delBase = __getDelegate(false);
                _CommentServiceDel __del = (_CommentServiceDel)__delBase;
                return __del.removeComment(appId, cid, commentIds, __ctx);
            }
            catch(IceInternal.LocalExceptionWrapper __ex)
            {
                __handleExceptionWrapper(__delBase, __ex);
            }
            catch(Ice.LocalException __ex)
            {
                __cnt = __handleException(__delBase, __ex, null, __cnt);
            }
        }
    }

    private static final String __removeComment_name = "removeComment";

    public Ice.AsyncResult begin_removeComment(String appId, String cid, String commentIds)
    {
        return begin_removeComment(appId, cid, commentIds, null, false, null);
    }

    public Ice.AsyncResult begin_removeComment(String appId, String cid, String commentIds, java.util.Map<String, String> __ctx)
    {
        return begin_removeComment(appId, cid, commentIds, __ctx, true, null);
    }

    public Ice.AsyncResult begin_removeComment(String appId, String cid, String commentIds, Ice.Callback __cb)
    {
        return begin_removeComment(appId, cid, commentIds, null, false, __cb);
    }

    public Ice.AsyncResult begin_removeComment(String appId, String cid, String commentIds, java.util.Map<String, String> __ctx, Ice.Callback __cb)
    {
        return begin_removeComment(appId, cid, commentIds, __ctx, true, __cb);
    }

    public Ice.AsyncResult begin_removeComment(String appId, String cid, String commentIds, Callback_CommentService_removeComment __cb)
    {
        return begin_removeComment(appId, cid, commentIds, null, false, __cb);
    }

    public Ice.AsyncResult begin_removeComment(String appId, String cid, String commentIds, java.util.Map<String, String> __ctx, Callback_CommentService_removeComment __cb)
    {
        return begin_removeComment(appId, cid, commentIds, __ctx, true, __cb);
    }

    private Ice.AsyncResult begin_removeComment(String appId, String cid, String commentIds, java.util.Map<String, String> __ctx, boolean __explicitCtx, IceInternal.CallbackBase __cb)
    {
        __checkAsyncTwowayOnly(__removeComment_name);
        IceInternal.OutgoingAsync __result = new IceInternal.OutgoingAsync(this, __removeComment_name, __cb);
        try
        {
            __result.__prepare(__removeComment_name, Ice.OperationMode.Normal, __ctx, __explicitCtx);
            IceInternal.BasicStream __os = __result.__os();
            __os.writeString(appId);
            __os.writeString(cid);
            __os.writeString(commentIds);
            __os.endWriteEncaps();
            __result.__send(true);
        }
        catch(Ice.LocalException __ex)
        {
            __result.__exceptionAsync(__ex);
        }
        return __result;
    }

    public cn.joy.ggg.api.model.CommentResponse end_removeComment(Ice.AsyncResult __result)
    {
        Ice.AsyncResult.__check(__result, this, __removeComment_name);
        if(!__result.__wait())
        {
            try
            {
                __result.__throwUserException();
            }
            catch(Ice.UserException __ex)
            {
                throw new Ice.UnknownUserException(__ex.ice_name());
            }
        }
        cn.joy.ggg.api.model.CommentResponse __ret;
        IceInternal.BasicStream __is = __result.__is();
        __is.startReadEncaps();
        __ret = new cn.joy.ggg.api.model.CommentResponse();
        __ret.__read(__is);
        __is.endReadEncaps();
        return __ret;
    }

    public static CommentServicePrx
    checkedCast(Ice.ObjectPrx __obj)
    {
        CommentServicePrx __d = null;
        if(__obj != null)
        {
            try
            {
                __d = (CommentServicePrx)__obj;
            }
            catch(ClassCastException ex)
            {
                if(__obj.ice_isA("::service::CommentService"))
                {
                    CommentServicePrxHelper __h = new CommentServicePrxHelper();
                    __h.__copyFrom(__obj);
                    __d = __h;
                }
            }
        }
        return __d;
    }

    public static CommentServicePrx
    checkedCast(Ice.ObjectPrx __obj, java.util.Map<String, String> __ctx)
    {
        CommentServicePrx __d = null;
        if(__obj != null)
        {
            try
            {
                __d = (CommentServicePrx)__obj;
            }
            catch(ClassCastException ex)
            {
                if(__obj.ice_isA("::service::CommentService", __ctx))
                {
                    CommentServicePrxHelper __h = new CommentServicePrxHelper();
                    __h.__copyFrom(__obj);
                    __d = __h;
                }
            }
        }
        return __d;
    }

    public static CommentServicePrx
    checkedCast(Ice.ObjectPrx __obj, String __facet)
    {
        CommentServicePrx __d = null;
        if(__obj != null)
        {
            Ice.ObjectPrx __bb = __obj.ice_facet(__facet);
            try
            {
                if(__bb.ice_isA("::service::CommentService"))
                {
                    CommentServicePrxHelper __h = new CommentServicePrxHelper();
                    __h.__copyFrom(__bb);
                    __d = __h;
                }
            }
            catch(Ice.FacetNotExistException ex)
            {
            }
        }
        return __d;
    }

    public static CommentServicePrx
    checkedCast(Ice.ObjectPrx __obj, String __facet, java.util.Map<String, String> __ctx)
    {
        CommentServicePrx __d = null;
        if(__obj != null)
        {
            Ice.ObjectPrx __bb = __obj.ice_facet(__facet);
            try
            {
                if(__bb.ice_isA("::service::CommentService", __ctx))
                {
                    CommentServicePrxHelper __h = new CommentServicePrxHelper();
                    __h.__copyFrom(__bb);
                    __d = __h;
                }
            }
            catch(Ice.FacetNotExistException ex)
            {
            }
        }
        return __d;
    }

    public static CommentServicePrx
    uncheckedCast(Ice.ObjectPrx __obj)
    {
        CommentServicePrx __d = null;
        if(__obj != null)
        {
            try
            {
                __d = (CommentServicePrx)__obj;
            }
            catch(ClassCastException ex)
            {
                CommentServicePrxHelper __h = new CommentServicePrxHelper();
                __h.__copyFrom(__obj);
                __d = __h;
            }
        }
        return __d;
    }

    public static CommentServicePrx
    uncheckedCast(Ice.ObjectPrx __obj, String __facet)
    {
        CommentServicePrx __d = null;
        if(__obj != null)
        {
            Ice.ObjectPrx __bb = __obj.ice_facet(__facet);
            CommentServicePrxHelper __h = new CommentServicePrxHelper();
            __h.__copyFrom(__bb);
            __d = __h;
        }
        return __d;
    }

    protected Ice._ObjectDelM
    __createDelegateM()
    {
        return new _CommentServiceDelM();
    }

    protected Ice._ObjectDelD
    __createDelegateD()
    {
        return new _CommentServiceDelD();
    }

    public static void
    __write(IceInternal.BasicStream __os, CommentServicePrx v)
    {
        __os.writeProxy(v);
    }

    public static CommentServicePrx
    __read(IceInternal.BasicStream __is)
    {
        Ice.ObjectPrx proxy = __is.readProxy();
        if(proxy != null)
        {
            CommentServicePrxHelper result = new CommentServicePrxHelper();
            result.__copyFrom(proxy);
            return result;
        }
        return null;
    }
}
