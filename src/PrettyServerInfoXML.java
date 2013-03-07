// Based on HTTPLoadBalancerServerInfoXML.java from LoadBalancer_2.0.zip
// package, provided by Wowza Media Systems, LLC

package cz.wejn.prettylb;

import java.io.OutputStream;
import java.util.*;

import com.wowza.wms.application.*;
import com.wowza.wms.http.*;
import com.wowza.wms.logging.*;
import com.wowza.wms.server.*;
import com.wowza.wms.vhost.*;

import com.wowza.wms.plugin.loadbalancer.*;

public class PrettyServerInfoXML extends HTTProvider2Base
{
	private LoadBalancerListener listener = null;
	private ILoadBalancerRedirector redirector = null;
	
	private ILoadBalancerRedirector getRedirector()
	{
		if (redirector == null)
		{
			while(true)
			{
				this.listener = (LoadBalancerListener)Server.getInstance().getProperties().get(ServerListenerLoadBalancerListener.PROP_LOADBALANCERLISTENER);
				if (this.listener == null)
				{
					WMSLoggerFactory.getLogger(PrettyServerInfoXML.class).warn("PrettyServerInfoXML.constructor: LoadBalancerListener not found.");
				}
				
				this.redirector = this.listener.getRedirector();
				if (this.redirector == null)
				{
					WMSLoggerFactory.getLogger(PrettyServerInfoXML.class).warn("PrettyServerInfoXML.constructor: ILoadBalancerRedirector not found.");
					break;
				}
				break;
			}
		}
		
		return redirector;
	}

	public void onHTTPRequest(IVHost vhost, IHTTPRequest req, IHTTPResponse resp)
	{
		if (!doHTTPAuthentication(vhost, req, resp))
			return;
		getRedirector();
		
		List<Map<String, Object>> info = this.redirector==null?null:this.redirector.getInfo();
		String retStr = LoadBalancerUtils.serverInfoToXMLStr(info);

		retStr = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
			"<?xml-stylesheet type=\"text/xsl\" href=\"#stylesheet\"?>" +
			"<!DOCTYPE LoadBalancerServerInfo [ " +
			"<!ATTLIST xsl:stylesheet id ID #REQUIRED> ]>\n" +
			retStr.replace("<LoadBalancerServerInfo>",
				"<LoadBalancerServerInfo>\n" +
				"<xsl:stylesheet id=\"stylesheet\" version=\"1.0\" xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\">" + 
				"<xsl:template match=\"/\">" + 
				"<html><body><h1>Wowza Edge Status</h1><table border=\"1\"><tr bgcolor=\"#bbddff\"><th>Node</th><th>GUID</th><th>Status</th><th>Connections</th><th>Redirects</th><th>Last Msg</th></tr>" +
				"<xsl:for-each select=\"LoadBalancerServerInfo/LoadBalancerServer\"><tr><td><xsl:value-of select=\"redirect\"/></td><td><xsl:value-of select=\"serverId\"/></td><td><xsl:value-of select=\"status\"/></td><td><xsl:value-of select=\"connectCount\"/></td><td><xsl:value-of select=\"redirectCount\"/></td><td><xsl:value-of select=\"lastMessage\"/></td></tr></xsl:for-each>" + 
				"</table></body></html>" +
				"</xsl:template>" +
				"</xsl:stylesheet>\n") + "\n";
		
		try
		{
			resp.setHeader("Refresh", "20");
			resp.setHeader("Content-Type", "text/xml");

			OutputStream out = resp.getOutputStream();
			byte[] outBytes = retStr.getBytes();
			out.write(outBytes);
		}
		catch (Exception e)
		{
			WMSLoggerFactory.getLogger(PrettyServerInfoXML.class).error("PrettyServerInfoXML: "+e.toString());
		}
		
	}

}
