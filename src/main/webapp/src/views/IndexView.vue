<!--
 Copyright (C) 2023 GIP-RECIA, Inc.

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
-->

<script setup lang="ts">
import {
  faArrowLeft,
} from '@fortawesome/free-solid-svg-icons'
import { FontAwesomeIcon } from '@fortawesome/vue-fontawesome'
import { useI18n } from 'vue-i18n'
import PageLayout from '@/components/PageLayout.vue'

const { t } = useI18n()

const {
  VITE_API_URI,
  VITE_RESSOURCES_DIFFUSABLES_API_URI,
  VITE_USER_INFO_API_URI,
  VITE_RESOURCES_PER_PAGE_DEFAULT,
} = import.meta.env
</script>

<template>
  <div class="container">
    <PageLayout
      :title="t('page.index.h1')"
    >
      <template #back>
        <a
          href="/portail"
          target="_self"
          class="btn-tertiary circle"
          title="Retour à l'accueil"
        >
          <FontAwesomeIcon
            :icon="faArrowLeft"
          />
        </a>
      </template>

      <ui-ressources-gar
        :base-api-url="VITE_API_URI"
        :ressources-diffusables-api-uri="VITE_RESSOURCES_DIFFUSABLES_API_URI"
        :user-info-api-url="VITE_USER_INFO_API_URI"
        :resources-per-page-default="VITE_RESOURCES_PER_PAGE_DEFAULT"
      />
    </PageLayout>
  </div>
</template>

<style scoped lang="scss">
@use 'sass:map';
@use '@/assets/scoped' as *;

.page-layout {
  > header > .heading > .btn-tertiary {
    align-self: flex-start;
    font-size: var(--#{$prefix}font-size-md);
  }
}
</style>
