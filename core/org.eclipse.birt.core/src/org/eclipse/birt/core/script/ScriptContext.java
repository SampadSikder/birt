/*******************************************************************************
 * Copyright (c) 2009 Actuate Corporation.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *  Actuate Corporation  - initial API and implementation
 *******************************************************************************/

package org.eclipse.birt.core.script;

import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.eclipse.birt.core.exception.BirtException;

import com.ibm.icu.util.TimeZone;

public class ScriptContext implements IScriptContext
{

	private Locale locale;
	private TimeZone timeZone;
	private ClassLoader applicationClassLoader;

	private ScriptContext parent;
	private Object scope;
	private Map<String, Object> attributes;

	private Map<String, IScriptEngine> engines;
	private Map<String, IScriptContext> scriptContexts;
	private ScriptEngineFactoryManager engineFactoryManager;

	public ScriptContext( )
	{
		this( null, null, null );
	}

	private ScriptContext( ScriptContext scriptContext, Object scope,
			Map<String, Object> attributes )
	{
		if ( scriptContext == null )
		{
			engines = new HashMap<String, IScriptEngine>( );
		}
		else
		{
			engines = scriptContext.engines;
		}
		this.attributes = new HashMap<String, Object>( );
		if ( attributes != null )
		{
			this.attributes.putAll( attributes );
		}
		parent = scriptContext;
		scriptContexts = new HashMap<String, IScriptContext>( );
		this.scope = scope;
		this.engineFactoryManager = new ScriptEngineFactoryManager( );
	}

	public ClassLoader getApplicationClassLoader( )
	{
		return applicationClassLoader;
	}

	public void setApplicationClassLoader( ClassLoader loader )
	{
		this.applicationClassLoader = loader;
		Collection<IScriptEngine> engineSet = engines.values( );
		for ( IScriptEngine engine : engineSet )
		{
			engine.setApplicationClassLoader( loader );
		}
	}

	public ScriptContext newContext( Object scope )
	{
		return newContext( scope, null );
	}

	public ScriptContext newContext( Object scope,
			Map<String, Object> attributes )
	{
		ScriptContext scriptContext = new ScriptContext( this, scope,
				attributes );
		return scriptContext;
	}

	public Map<String, Object> getAttributes( )
	{
		return attributes;
	}

	public void setAttributes( Map<String, Object> attributes )
	{
		if ( attributes != null )
		{
			this.attributes.putAll( attributes );
		}
	}

	public void setAttribute( String name, Object value )
	{
		attributes.put( name, value );
	}

	public void removeAttribute( String name )
	{
		attributes.remove( name );
	}

	public ICompiledScript compile( String language, String fileName,
			int lineNo, String script ) throws BirtException
	{
		assert ( language != null );
		IScriptEngine engine = getScriptEngine( language );
		return engine.compile( this, fileName, lineNo, script );
	}

	public Object evaluate( ICompiledScript script ) throws BirtException
	{
		IScriptEngine engine = getScriptEngine( script.getLanguage( ) );
		return engine.evaluate( this, script );
	}

	public void setLocale( Locale locale )
	{
		this.locale = locale;
		Collection<IScriptEngine> engineSet = engines.values( );
		for ( IScriptEngine engine : engineSet )
		{
			engine.setLocale( locale );
		}
	}

	public Locale getLocale( )
	{
		return locale;
	}

	public void setTimeZone( TimeZone timeZone )
	{
		this.timeZone = timeZone;
		Collection<IScriptEngine> engineSet = engines.values( );
		for ( IScriptEngine engine : engineSet )
		{
			engine.setTimeZone( timeZone );
		}
	}

	public TimeZone getTimeZone( )
	{
		return timeZone;
	}

	public void close( )
	{
		if ( parent == null )
		{
			Collection<IScriptEngine> engineSet = engines.values( );
			for ( IScriptEngine engine : engineSet )
			{
				engine.close( );
			}
		}
	}

	public IScriptEngine getScriptEngine( String scriptName )
			throws BirtException
	{
		if ( scriptName == null )
		{
			throw new NullPointerException( );
		}
		if ( engines.containsKey( scriptName ) )
		{
			return engines.get( scriptName );
		}
		IScriptEngineFactory factory = engineFactoryManager
				.getScriptEngineFactory( scriptName );
		if ( factory == null )
		{
			throw new BirtException( "No such script extension : " + scriptName );
		}
		return createEngine( factory );
	}

	public ScriptContext getParent( )
	{
		return parent;
	}

	private IScriptEngine createEngine( IScriptEngineFactory factory )
			throws BirtException
	{
		IScriptEngine scriptEngine = factory.createScriptEngine( );
		scriptEngine.setLocale( locale );
		scriptEngine.setTimeZone( timeZone );
		scriptEngine.setApplicationClassLoader( applicationClassLoader );
		engines.put( factory.getScriptLanguage( ), scriptEngine );
		return scriptEngine;
	}

	public Object getScopeObject( )
	{
		return scope;
	}

	public IScriptContext getScriptContext( String language )
	{
		return scriptContexts.get( language );
	}

	public void setScriptContext( String language, IScriptContext scriptContext )
	{
		scriptContexts.put( language, scriptContext );
	}
}
