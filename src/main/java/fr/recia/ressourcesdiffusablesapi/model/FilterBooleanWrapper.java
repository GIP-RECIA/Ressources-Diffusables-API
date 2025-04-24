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
            filterBoolean = FilterBoolean.UNKNOWN;
        }else if(bool){
            filterBoolean = FilterBoolean.TRUE;
        }else {
            filterBoolean = FilterBoolean.FALSE;
        }
    }

    public FilterBooleanWrapper(FilterBoolean filterBoolean){
      this.filterBoolean = filterBoolean;
    }

    public FilterBoolean matchs(boolean value){
        if(this.filterBoolean == FilterBoolean.UNKNOWN){
            return FilterBoolean.UNKNOWN;
        }else if(this.filterBoolean == FilterBoolean.TRUE && value){
            return FilterBoolean.TRUE;
        }else if(this.filterBoolean == FilterBoolean.FALSE && !value) {
            return FilterBoolean.TRUE;
        }else {
            return FilterBoolean.FALSE;
        }
    }

    public boolean nonUnknownValueAsBoolean(){
        switch (this.filterBoolean){
            case TRUE:
                return true;
            case FALSE:
                return false;
            case UNKNOWN:
                throw new RuntimeException("Cannot convert UNKNOWN to boolean value");
            default:
                throw new RuntimeException(String.format("Cannot convert %S to boolean value",this.filterBoolean));
        }
    }

    @Override
    public boolean equals(Object other){
        if(!(other instanceof FilterBooleanWrapper)){
            return false;
        }
        if(this.getFilterBoolean() == FilterBoolean.UNKNOWN){
            return true;
        }
        FilterBooleanWrapper castedOther = (FilterBooleanWrapper) other;
        if(castedOther.getFilterBoolean() == FilterBoolean.UNKNOWN){
            return true;
        }
        return ((FilterBooleanWrapper) other).getFilterBoolean().equals(this.getFilterBoolean());
    }

}
