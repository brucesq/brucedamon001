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
// Generated from file `community.ice'
//
// Warning: do not edit this file.
//
// </auto-generated>


public final class _CommunityServiceDelD extends Ice._ObjectDelD implements _CommunityServiceDel
{
    public int
    addCollect(final String userid, final String title, final String link, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper
    {
        final Ice.Current __current = new Ice.Current();
        __initCurrent(__current, "addCollect", Ice.OperationMode.Normal, __ctx);
        final Ice.IntHolder __result = new Ice.IntHolder();
        IceInternal.Direct __direct = null;
        try
        {
            __direct = new IceInternal.Direct(__current)
            {
                public Ice.DispatchStatus run(Ice.Object __obj)
                {
                    CommunityService __servant = null;
                    try
                    {
                        __servant = (CommunityService)__obj;
                    }
                    catch(ClassCastException __ex)
                    {
                        throw new Ice.OperationNotExistException(__current.id, __current.facet, __current.operation);
                    }
                    __result.value = __servant.addCollect(userid, title, link, __current);
                    return Ice.DispatchStatus.DispatchOK;
                }
            };
            try
            {
                Ice.DispatchStatus __status = __direct.servant().__collocDispatch(__direct);
                if(__status == Ice.DispatchStatus.DispatchUserException)
                {
                    __direct.throwUserException();
                }
                assert __status == Ice.DispatchStatus.DispatchOK;
                return __result.value;
            }
            finally
            {
                __direct.destroy();
            }
        }
        catch(Ice.SystemException __ex)
        {
            throw __ex;
        }
        catch(java.lang.Throwable __ex)
        {
            IceInternal.LocalExceptionWrapper.throwWrapper(__ex);
        }
        return __result.value;
    }

    public int
    addDevelopment(final cn.joy.ggg.api.model.FriendDevelopment devel, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper
    {
        final Ice.Current __current = new Ice.Current();
        __initCurrent(__current, "addDevelopment", Ice.OperationMode.Normal, __ctx);
        final Ice.IntHolder __result = new Ice.IntHolder();
        IceInternal.Direct __direct = null;
        try
        {
            __direct = new IceInternal.Direct(__current)
            {
                public Ice.DispatchStatus run(Ice.Object __obj)
                {
                    CommunityService __servant = null;
                    try
                    {
                        __servant = (CommunityService)__obj;
                    }
                    catch(ClassCastException __ex)
                    {
                        throw new Ice.OperationNotExistException(__current.id, __current.facet, __current.operation);
                    }
                    __result.value = __servant.addDevelopment(devel, __current);
                    return Ice.DispatchStatus.DispatchOK;
                }
            };
            try
            {
                Ice.DispatchStatus __status = __direct.servant().__collocDispatch(__direct);
                if(__status == Ice.DispatchStatus.DispatchUserException)
                {
                    __direct.throwUserException();
                }
                assert __status == Ice.DispatchStatus.DispatchOK;
                return __result.value;
            }
            finally
            {
                __direct.destroy();
            }
        }
        catch(Ice.SystemException __ex)
        {
            throw __ex;
        }
        catch(java.lang.Throwable __ex)
        {
            IceInternal.LocalExceptionWrapper.throwWrapper(__ex);
        }
        return __result.value;
    }

    public int
    addTopic(final String userid, final cn.joy.ggg.api.model.Topic tipoc, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper
    {
        final Ice.Current __current = new Ice.Current();
        __initCurrent(__current, "addTopic", Ice.OperationMode.Normal, __ctx);
        final Ice.IntHolder __result = new Ice.IntHolder();
        IceInternal.Direct __direct = null;
        try
        {
            __direct = new IceInternal.Direct(__current)
            {
                public Ice.DispatchStatus run(Ice.Object __obj)
                {
                    CommunityService __servant = null;
                    try
                    {
                        __servant = (CommunityService)__obj;
                    }
                    catch(ClassCastException __ex)
                    {
                        throw new Ice.OperationNotExistException(__current.id, __current.facet, __current.operation);
                    }
                    __result.value = __servant.addTopic(userid, tipoc, __current);
                    return Ice.DispatchStatus.DispatchOK;
                }
            };
            try
            {
                Ice.DispatchStatus __status = __direct.servant().__collocDispatch(__direct);
                if(__status == Ice.DispatchStatus.DispatchUserException)
                {
                    __direct.throwUserException();
                }
                assert __status == Ice.DispatchStatus.DispatchOK;
                return __result.value;
            }
            finally
            {
                __direct.destroy();
            }
        }
        catch(Ice.SystemException __ex)
        {
            throw __ex;
        }
        catch(java.lang.Throwable __ex)
        {
            IceInternal.LocalExceptionWrapper.throwWrapper(__ex);
        }
        return __result.value;
    }

    public void
    encodeNotify(final cn.joy.ggg.api.model.Topic topic, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper
    {
        final Ice.Current __current = new Ice.Current();
        __initCurrent(__current, "encodeNotify", Ice.OperationMode.Normal, __ctx);
        IceInternal.Direct __direct = null;
        try
        {
            __direct = new IceInternal.Direct(__current)
            {
                public Ice.DispatchStatus run(Ice.Object __obj)
                {
                    CommunityService __servant = null;
                    try
                    {
                        __servant = (CommunityService)__obj;
                    }
                    catch(ClassCastException __ex)
                    {
                        throw new Ice.OperationNotExistException(__current.id, __current.facet, __current.operation);
                    }
                    __servant.encodeNotify(topic, __current);
                    return Ice.DispatchStatus.DispatchOK;
                }
            };
            try
            {
                Ice.DispatchStatus __status = __direct.servant().__collocDispatch(__direct);
                if(__status == Ice.DispatchStatus.DispatchUserException)
                {
                    __direct.throwUserException();
                }
                assert __status == Ice.DispatchStatus.DispatchOK;
            }
            finally
            {
                __direct.destroy();
            }
        }
        catch(Ice.SystemException __ex)
        {
            throw __ex;
        }
        catch(java.lang.Throwable __ex)
        {
            IceInternal.LocalExceptionWrapper.throwWrapper(__ex);
        }
    }

    public cn.joy.ggg.api.model.PageSupport
    getFriendDevelopment(final String userId, final int pageNo, final int pageSize, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper
    {
        final Ice.Current __current = new Ice.Current();
        __initCurrent(__current, "getFriendDevelopment", Ice.OperationMode.Normal, __ctx);
        final cn.joy.ggg.api.model.PageSupportHolder __result = new cn.joy.ggg.api.model.PageSupportHolder();
        IceInternal.Direct __direct = null;
        try
        {
            __direct = new IceInternal.Direct(__current)
            {
                public Ice.DispatchStatus run(Ice.Object __obj)
                {
                    CommunityService __servant = null;
                    try
                    {
                        __servant = (CommunityService)__obj;
                    }
                    catch(ClassCastException __ex)
                    {
                        throw new Ice.OperationNotExistException(__current.id, __current.facet, __current.operation);
                    }
                    __result.value = __servant.getFriendDevelopment(userId, pageNo, pageSize, __current);
                    return Ice.DispatchStatus.DispatchOK;
                }
            };
            try
            {
                Ice.DispatchStatus __status = __direct.servant().__collocDispatch(__direct);
                if(__status == Ice.DispatchStatus.DispatchUserException)
                {
                    __direct.throwUserException();
                }
                assert __status == Ice.DispatchStatus.DispatchOK;
                return __result.value;
            }
            finally
            {
                __direct.destroy();
            }
        }
        catch(Ice.SystemException __ex)
        {
            throw __ex;
        }
        catch(java.lang.Throwable __ex)
        {
            IceInternal.LocalExceptionWrapper.throwWrapper(__ex);
        }
        return __result.value;
    }

    public cn.joy.ggg.api.model.PageSupport
    getList(final String userid, final int pageNo, final int pageSize, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper
    {
        final Ice.Current __current = new Ice.Current();
        __initCurrent(__current, "getList", Ice.OperationMode.Normal, __ctx);
        final cn.joy.ggg.api.model.PageSupportHolder __result = new cn.joy.ggg.api.model.PageSupportHolder();
        IceInternal.Direct __direct = null;
        try
        {
            __direct = new IceInternal.Direct(__current)
            {
                public Ice.DispatchStatus run(Ice.Object __obj)
                {
                    CommunityService __servant = null;
                    try
                    {
                        __servant = (CommunityService)__obj;
                    }
                    catch(ClassCastException __ex)
                    {
                        throw new Ice.OperationNotExistException(__current.id, __current.facet, __current.operation);
                    }
                    __result.value = __servant.getList(userid, pageNo, pageSize, __current);
                    return Ice.DispatchStatus.DispatchOK;
                }
            };
            try
            {
                Ice.DispatchStatus __status = __direct.servant().__collocDispatch(__direct);
                if(__status == Ice.DispatchStatus.DispatchUserException)
                {
                    __direct.throwUserException();
                }
                assert __status == Ice.DispatchStatus.DispatchOK;
                return __result.value;
            }
            finally
            {
                __direct.destroy();
            }
        }
        catch(Ice.SystemException __ex)
        {
            throw __ex;
        }
        catch(java.lang.Throwable __ex)
        {
            IceInternal.LocalExceptionWrapper.throwWrapper(__ex);
        }
        return __result.value;
    }

    public cn.joy.ggg.api.model.PageSupport
    getScoringHistory(final String userid, final int pageNo, final int pageSize, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper
    {
        final Ice.Current __current = new Ice.Current();
        __initCurrent(__current, "getScoringHistory", Ice.OperationMode.Normal, __ctx);
        final cn.joy.ggg.api.model.PageSupportHolder __result = new cn.joy.ggg.api.model.PageSupportHolder();
        IceInternal.Direct __direct = null;
        try
        {
            __direct = new IceInternal.Direct(__current)
            {
                public Ice.DispatchStatus run(Ice.Object __obj)
                {
                    CommunityService __servant = null;
                    try
                    {
                        __servant = (CommunityService)__obj;
                    }
                    catch(ClassCastException __ex)
                    {
                        throw new Ice.OperationNotExistException(__current.id, __current.facet, __current.operation);
                    }
                    __result.value = __servant.getScoringHistory(userid, pageNo, pageSize, __current);
                    return Ice.DispatchStatus.DispatchOK;
                }
            };
            try
            {
                Ice.DispatchStatus __status = __direct.servant().__collocDispatch(__direct);
                if(__status == Ice.DispatchStatus.DispatchUserException)
                {
                    __direct.throwUserException();
                }
                assert __status == Ice.DispatchStatus.DispatchOK;
                return __result.value;
            }
            finally
            {
                __direct.destroy();
            }
        }
        catch(Ice.SystemException __ex)
        {
            throw __ex;
        }
        catch(java.lang.Throwable __ex)
        {
            IceInternal.LocalExceptionWrapper.throwWrapper(__ex);
        }
        return __result.value;
    }

    public cn.joy.ggg.api.model.PageSupport
    getSelfDevelopment(final String selfId, final int pageNo, final int pageSize, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper
    {
        final Ice.Current __current = new Ice.Current();
        __initCurrent(__current, "getSelfDevelopment", Ice.OperationMode.Normal, __ctx);
        final cn.joy.ggg.api.model.PageSupportHolder __result = new cn.joy.ggg.api.model.PageSupportHolder();
        IceInternal.Direct __direct = null;
        try
        {
            __direct = new IceInternal.Direct(__current)
            {
                public Ice.DispatchStatus run(Ice.Object __obj)
                {
                    CommunityService __servant = null;
                    try
                    {
                        __servant = (CommunityService)__obj;
                    }
                    catch(ClassCastException __ex)
                    {
                        throw new Ice.OperationNotExistException(__current.id, __current.facet, __current.operation);
                    }
                    __result.value = __servant.getSelfDevelopment(selfId, pageNo, pageSize, __current);
                    return Ice.DispatchStatus.DispatchOK;
                }
            };
            try
            {
                Ice.DispatchStatus __status = __direct.servant().__collocDispatch(__direct);
                if(__status == Ice.DispatchStatus.DispatchUserException)
                {
                    __direct.throwUserException();
                }
                assert __status == Ice.DispatchStatus.DispatchOK;
                return __result.value;
            }
            finally
            {
                __direct.destroy();
            }
        }
        catch(Ice.SystemException __ex)
        {
            throw __ex;
        }
        catch(java.lang.Throwable __ex)
        {
            IceInternal.LocalExceptionWrapper.throwWrapper(__ex);
        }
        return __result.value;
    }

    public cn.joy.ggg.api.model.PageSupport
    getTopicList(final String userid, final int pageNo, final int pageSize, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper
    {
        final Ice.Current __current = new Ice.Current();
        __initCurrent(__current, "getTopicList", Ice.OperationMode.Normal, __ctx);
        final cn.joy.ggg.api.model.PageSupportHolder __result = new cn.joy.ggg.api.model.PageSupportHolder();
        IceInternal.Direct __direct = null;
        try
        {
            __direct = new IceInternal.Direct(__current)
            {
                public Ice.DispatchStatus run(Ice.Object __obj)
                {
                    CommunityService __servant = null;
                    try
                    {
                        __servant = (CommunityService)__obj;
                    }
                    catch(ClassCastException __ex)
                    {
                        throw new Ice.OperationNotExistException(__current.id, __current.facet, __current.operation);
                    }
                    __result.value = __servant.getTopicList(userid, pageNo, pageSize, __current);
                    return Ice.DispatchStatus.DispatchOK;
                }
            };
            try
            {
                Ice.DispatchStatus __status = __direct.servant().__collocDispatch(__direct);
                if(__status == Ice.DispatchStatus.DispatchUserException)
                {
                    __direct.throwUserException();
                }
                assert __status == Ice.DispatchStatus.DispatchOK;
                return __result.value;
            }
            finally
            {
                __direct.destroy();
            }
        }
        catch(Ice.SystemException __ex)
        {
            throw __ex;
        }
        catch(java.lang.Throwable __ex)
        {
            IceInternal.LocalExceptionWrapper.throwWrapper(__ex);
        }
        return __result.value;
    }

    public cn.joy.ggg.api.model.UserProfile
    getUser(final String userid, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper
    {
        final Ice.Current __current = new Ice.Current();
        __initCurrent(__current, "getUser", Ice.OperationMode.Normal, __ctx);
        final cn.joy.ggg.api.model.UserProfileHolder __result = new cn.joy.ggg.api.model.UserProfileHolder();
        IceInternal.Direct __direct = null;
        try
        {
            __direct = new IceInternal.Direct(__current)
            {
                public Ice.DispatchStatus run(Ice.Object __obj)
                {
                    CommunityService __servant = null;
                    try
                    {
                        __servant = (CommunityService)__obj;
                    }
                    catch(ClassCastException __ex)
                    {
                        throw new Ice.OperationNotExistException(__current.id, __current.facet, __current.operation);
                    }
                    __result.value = __servant.getUser(userid, __current);
                    return Ice.DispatchStatus.DispatchOK;
                }
            };
            try
            {
                Ice.DispatchStatus __status = __direct.servant().__collocDispatch(__direct);
                if(__status == Ice.DispatchStatus.DispatchUserException)
                {
                    __direct.throwUserException();
                }
                assert __status == Ice.DispatchStatus.DispatchOK;
                return __result.value;
            }
            finally
            {
                __direct.destroy();
            }
        }
        catch(Ice.SystemException __ex)
        {
            throw __ex;
        }
        catch(java.lang.Throwable __ex)
        {
            IceInternal.LocalExceptionWrapper.throwWrapper(__ex);
        }
        return __result.value;
    }

    public int
    removeCollect(final String userid, final String collectid, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper
    {
        final Ice.Current __current = new Ice.Current();
        __initCurrent(__current, "removeCollect", Ice.OperationMode.Normal, __ctx);
        final Ice.IntHolder __result = new Ice.IntHolder();
        IceInternal.Direct __direct = null;
        try
        {
            __direct = new IceInternal.Direct(__current)
            {
                public Ice.DispatchStatus run(Ice.Object __obj)
                {
                    CommunityService __servant = null;
                    try
                    {
                        __servant = (CommunityService)__obj;
                    }
                    catch(ClassCastException __ex)
                    {
                        throw new Ice.OperationNotExistException(__current.id, __current.facet, __current.operation);
                    }
                    __result.value = __servant.removeCollect(userid, collectid, __current);
                    return Ice.DispatchStatus.DispatchOK;
                }
            };
            try
            {
                Ice.DispatchStatus __status = __direct.servant().__collocDispatch(__direct);
                if(__status == Ice.DispatchStatus.DispatchUserException)
                {
                    __direct.throwUserException();
                }
                assert __status == Ice.DispatchStatus.DispatchOK;
                return __result.value;
            }
            finally
            {
                __direct.destroy();
            }
        }
        catch(Ice.SystemException __ex)
        {
            throw __ex;
        }
        catch(java.lang.Throwable __ex)
        {
            IceInternal.LocalExceptionWrapper.throwWrapper(__ex);
        }
        return __result.value;
    }

    public int
    removeTopic(final String userid, final String tid, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper
    {
        final Ice.Current __current = new Ice.Current();
        __initCurrent(__current, "removeTopic", Ice.OperationMode.Normal, __ctx);
        final Ice.IntHolder __result = new Ice.IntHolder();
        IceInternal.Direct __direct = null;
        try
        {
            __direct = new IceInternal.Direct(__current)
            {
                public Ice.DispatchStatus run(Ice.Object __obj)
                {
                    CommunityService __servant = null;
                    try
                    {
                        __servant = (CommunityService)__obj;
                    }
                    catch(ClassCastException __ex)
                    {
                        throw new Ice.OperationNotExistException(__current.id, __current.facet, __current.operation);
                    }
                    __result.value = __servant.removeTopic(userid, tid, __current);
                    return Ice.DispatchStatus.DispatchOK;
                }
            };
            try
            {
                Ice.DispatchStatus __status = __direct.servant().__collocDispatch(__direct);
                if(__status == Ice.DispatchStatus.DispatchUserException)
                {
                    __direct.throwUserException();
                }
                assert __status == Ice.DispatchStatus.DispatchOK;
                return __result.value;
            }
            finally
            {
                __direct.destroy();
            }
        }
        catch(Ice.SystemException __ex)
        {
            throw __ex;
        }
        catch(java.lang.Throwable __ex)
        {
            IceInternal.LocalExceptionWrapper.throwWrapper(__ex);
        }
        return __result.value;
    }

    public java.util.List<cn.joy.ggg.api.model.UserProfile>
    searchUser(final cn.joy.ggg.api.model.UserProfile user, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper
    {
        final Ice.Current __current = new Ice.Current();
        __initCurrent(__current, "searchUser", Ice.OperationMode.Normal, __ctx);
        final cn.joy.ggg.api.model.ProfileListHolder __result = new cn.joy.ggg.api.model.ProfileListHolder();
        IceInternal.Direct __direct = null;
        try
        {
            __direct = new IceInternal.Direct(__current)
            {
                public Ice.DispatchStatus run(Ice.Object __obj)
                {
                    CommunityService __servant = null;
                    try
                    {
                        __servant = (CommunityService)__obj;
                    }
                    catch(ClassCastException __ex)
                    {
                        throw new Ice.OperationNotExistException(__current.id, __current.facet, __current.operation);
                    }
                    __result.value = __servant.searchUser(user, __current);
                    return Ice.DispatchStatus.DispatchOK;
                }
            };
            try
            {
                Ice.DispatchStatus __status = __direct.servant().__collocDispatch(__direct);
                if(__status == Ice.DispatchStatus.DispatchUserException)
                {
                    __direct.throwUserException();
                }
                assert __status == Ice.DispatchStatus.DispatchOK;
                return __result.value;
            }
            finally
            {
                __direct.destroy();
            }
        }
        catch(Ice.SystemException __ex)
        {
            throw __ex;
        }
        catch(java.lang.Throwable __ex)
        {
            IceInternal.LocalExceptionWrapper.throwWrapper(__ex);
        }
        return __result.value;
    }

    public int
    setTopic(final String userid, final cn.joy.ggg.api.model.Topic topic, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper
    {
        final Ice.Current __current = new Ice.Current();
        __initCurrent(__current, "setTopic", Ice.OperationMode.Normal, __ctx);
        final Ice.IntHolder __result = new Ice.IntHolder();
        IceInternal.Direct __direct = null;
        try
        {
            __direct = new IceInternal.Direct(__current)
            {
                public Ice.DispatchStatus run(Ice.Object __obj)
                {
                    CommunityService __servant = null;
                    try
                    {
                        __servant = (CommunityService)__obj;
                    }
                    catch(ClassCastException __ex)
                    {
                        throw new Ice.OperationNotExistException(__current.id, __current.facet, __current.operation);
                    }
                    __result.value = __servant.setTopic(userid, topic, __current);
                    return Ice.DispatchStatus.DispatchOK;
                }
            };
            try
            {
                Ice.DispatchStatus __status = __direct.servant().__collocDispatch(__direct);
                if(__status == Ice.DispatchStatus.DispatchUserException)
                {
                    __direct.throwUserException();
                }
                assert __status == Ice.DispatchStatus.DispatchOK;
                return __result.value;
            }
            finally
            {
                __direct.destroy();
            }
        }
        catch(Ice.SystemException __ex)
        {
            throw __ex;
        }
        catch(java.lang.Throwable __ex)
        {
            IceInternal.LocalExceptionWrapper.throwWrapper(__ex);
        }
        return __result.value;
    }

    public int
    setUser(final cn.joy.ggg.api.model.UserProfile user, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper
    {
        final Ice.Current __current = new Ice.Current();
        __initCurrent(__current, "setUser", Ice.OperationMode.Normal, __ctx);
        final Ice.IntHolder __result = new Ice.IntHolder();
        IceInternal.Direct __direct = null;
        try
        {
            __direct = new IceInternal.Direct(__current)
            {
                public Ice.DispatchStatus run(Ice.Object __obj)
                {
                    CommunityService __servant = null;
                    try
                    {
                        __servant = (CommunityService)__obj;
                    }
                    catch(ClassCastException __ex)
                    {
                        throw new Ice.OperationNotExistException(__current.id, __current.facet, __current.operation);
                    }
                    __result.value = __servant.setUser(user, __current);
                    return Ice.DispatchStatus.DispatchOK;
                }
            };
            try
            {
                Ice.DispatchStatus __status = __direct.servant().__collocDispatch(__direct);
                if(__status == Ice.DispatchStatus.DispatchUserException)
                {
                    __direct.throwUserException();
                }
                assert __status == Ice.DispatchStatus.DispatchOK;
                return __result.value;
            }
            finally
            {
                __direct.destroy();
            }
        }
        catch(Ice.SystemException __ex)
        {
            throw __ex;
        }
        catch(java.lang.Throwable __ex)
        {
            IceInternal.LocalExceptionWrapper.throwWrapper(__ex);
        }
        return __result.value;
    }
}