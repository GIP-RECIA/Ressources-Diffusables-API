/*
 * Copyright (C) 2021 GIP-RECIA, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *                 http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package fr.recia.ressourcesdiffusablesapi.model;

import fr.recia.ressourcesdiffusablesapi.enums.FilterBoolean;
import lombok.Getter;

import java.util.Objects;

@Getter
public class FilterBooleanWrapper {

    private final FilterBoolean filterBoolean;

    public FilterBooleanWrapper(Boolean bool){
        if(Objects.isNull(bool)){
            filterBoolean = FilterBoolean.IGNORED;
        }else if(bool){
            filterBoolean = FilterBoolean.TRUE;
        }else {
            filterBoolean = FilterBoolean.FALSE;
        }
    }

    public boolean compare(boolean value){
        return this.getFilterBoolean() == FilterBoolean.IGNORED || (this.getFilterBoolean() == FilterBoolean.TRUE && value) || (this.getFilterBoolean() == FilterBoolean.FALSE && !value);
    }

    @Override
    public boolean equals(Object other){
        if(!(other instanceof FilterBooleanWrapper)){
            return false;
        }
        if(this.getFilterBoolean() == FilterBoolean.IGNORED){
            return true;
        }
        FilterBooleanWrapper castedOther = (FilterBooleanWrapper) other;
        if(castedOther.getFilterBoolean() == FilterBoolean.IGNORED){
            return true;
        }
        return ((FilterBooleanWrapper) other).getFilterBoolean().equals(this.getFilterBoolean());
    }

}
