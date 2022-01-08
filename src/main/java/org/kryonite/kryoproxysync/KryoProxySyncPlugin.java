package org.kryonite.kryoproxysync;

import com.google.inject.Inject;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;

@Plugin(id = "kryo-proxy-sync", name = "Kryo Proxy Sync", authors = "Kryonite Labs", version = "0.1.0")
public class KryoProxySyncPlugin {

  private final ProxyServer server;

  @Inject
  public KryoProxySyncPlugin(ProxyServer server) {
    this.server = server;
  }
}
