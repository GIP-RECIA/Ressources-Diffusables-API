package fr.recia.ressourcesdiffusablesapi.service.cache;

import fr.recia.ressourcesdiffusablesapi.model.RessourceDiffusable;
import fr.recia.ressourcesdiffusablesapi.model.RessourceDiffusableFilter;
import lombok.extern.slf4j.Slf4j;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

import java.util.List;

@Slf4j
public class ServiceCacheHistoriqueEhcache implements ServiceCacheHistorique{

    Cache cache = CacheManager.getInstance().getCache("cacheRessourcesDiffusables");

    @Override
    public List<RessourceDiffusable> get(RessourceDiffusableFilter filter) {
        Element element = this.cache.get(filter);

        return element == null ? null : (List<RessourceDiffusable>) element.getObjectValue();
    }

    @Override
    public void put(RessourceDiffusableFilter filter, List<RessourceDiffusable> ressourcesDiffusables) {
        this.cache.put(new Element(filter, ressourcesDiffusables));
        if (log.isDebugEnabled()) log.debug("Cache: Ressource diffusable put in history using Ehcache; new size of history is " + this.cache.getSize());
    }

    @Override
    public int size() {
        return CacheManager.getInstance().getCache("cacheRessourcesDiffusables").getSize();
    }

    @Override
    public void clear() {
        int count = this.cache.getSize();
        this.cache.removeAll();
        if (log.isDebugEnabled()) log.debug("Cache: History cleared using Ehcache; " + count + " elements removed");
    }
}
