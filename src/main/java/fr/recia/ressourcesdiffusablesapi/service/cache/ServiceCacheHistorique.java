package fr.recia.ressourcesdiffusablesapi.service.cache;

import fr.recia.ressourcesdiffusablesapi.model.RessourceDiffusable;
import fr.recia.ressourcesdiffusablesapi.model.RessourceDiffusableFilter;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ServiceCacheHistorique {

    /**
     * Retourne la liste de ressources diffusables correspondant au filtre si cette dernière a été mise en cache.
     * @param filter Filtre pour lequel on recherche la liste correspondante
     * @return La liste de ressources diffusables, ou null si ce filtre ne correspond à aucune liste mise en cache
     */
    List<RessourceDiffusable> get(RessourceDiffusableFilter filter);

    /**
     * Met en cache la liste de ressources diffusables au filtre correspondant.
     * @param filter Filtre clé
     * @param ressourcesDiffusables Liste de ressources diffusables
     */
    void put(RessourceDiffusableFilter filter, List<RessourceDiffusable> ressourcesDiffusables);

    /**
     * Retourne le nombre de listes de ressources diffusables actuellement en cache.
     * @return Nombre de listes de ressources diffusables actuellement en cache
     */
    int size();

    /**
     * Vide entièrement le cache en supprimant toutes les listes de ressources diffusables qu'il contient.
     */
    void clear();
}
