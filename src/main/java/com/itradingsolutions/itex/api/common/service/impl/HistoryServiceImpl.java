package com.itradingsolutions.itex.api.common.service.impl;

import com.itradingsolutions.itex.api.admin.user.models.entities.UserEntity;
import com.itradingsolutions.itex.api.admin.user.services.IUserService;
import com.itradingsolutions.itex.api.common.exceptions.HistoryException;
import com.itradingsolutions.itex.api.masters.common.models.dto.BaseMasterDTO;
import org.hibernate.MappingException;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;

public abstract class HistoryServiceImpl {

    @Autowired
    private IUserService userService;

    private static final Set<String> IGNORED_FIELDS =
            Set.of("createdBy", "updatedBy", "openBy", "menus", "actions");

    private static final Set<String> INNER_ALLOWED_FIELDS =
            Set.of("id", "name", "code", "description", "fullName", "nameShort");

    protected UserEntity getUserAuthUser() {
        return userService.getUserAuthenticated();
    }

    protected void putIfChanged(Map<String, Object> map, String key, Object oldValue, Object newValue) {
        if (!Objects.equals(oldValue, newValue)) {
            map.put(key, getChangeItem(oldValue, newValue));
        }
    }

    protected void compareMaster(Map<String, Object> map, String key, BaseMasterDTO oldObj, BaseMasterDTO newObj) {
        UUID oldId = oldObj != null ? oldObj.getId() : null;
        UUID newId = newObj != null ? newObj.getId() : null;

        String oldName = oldObj != null ? oldObj.getName() : null;
        String newName = newObj != null ? newObj.getName() : null;

        if (!Objects.equals(oldId, newId)) {
            map.put(key, getChangeItem(oldName, newName));
        }
    }

    protected void compareOther(Map<String, Object> map, String key, UUID oldId, UUID newId, String oldName, String newName) {
         if (!Objects.equals(oldId, newId)) {
            map.put(key, getChangeItem(oldName, newName));
        }
    }

    protected static <T, R> R safeGet(T obj, Function<T, R> mapper) {
        return Optional.ofNullable(obj).map(mapper).orElse(null);
    }

    protected Map<String, Object> getChangeItem(Object oldValue, Object newValue) {
        Map<String, Object> changes = new HashMap<>();
        changes.put("old", oldValue);
        changes.put("new", newValue);
        return changes;
    }

    protected void putIfChangedBigDecimal(Map<String, Object> changes, String field, BigDecimal oldValue, BigDecimal newValue) {
        if (oldValue == null && newValue == null) return;
        if (oldValue == null || newValue == null || oldValue.compareTo(newValue) != 0) {
            changes.put(field, getChangeItem(oldValue, newValue));
        }
    }

    protected Object getFieldValue(Object dto, String fieldName) {
        try {
            Field field = dto.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            return field.get(dto);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            return null;
        }
    }

    protected void validateNotNull(Object obj, String message) {
        if (obj == null) {
            throw new HistoryException(message);
        }
    }

    protected Map<String, Object> convertToMap(Object obj, boolean isMain, boolean addLists) {
        if (obj == null) return Collections.emptyMap();
        Map<String, Object> result = new HashMap<>();


        for (Field field : getAllFields(obj.getClass())) {
            String fieldName = field.getName();
            // Saltar campos ignorados
            if (IGNORED_FIELDS.stream().anyMatch(f -> f.equalsIgnoreCase(fieldName))) continue;

            field.setAccessible(true);

            Object value;
            try {
                value = field.get(obj);
            } catch (IllegalAccessException e) {
                throw new MappingException("Error accediendo al campo: " + fieldName, e);
            }

            if (value == null) continue;

            // --- 1️⃣ Si es una lista ---
            if (value instanceof List<?>) {
                handleListField(result, fieldName, (List<?>) value, addLists);
            }
            // --- 2️⃣ Si es un objeto complejo ---
            else if (isComplexObject(value)) {
                handleComplexField(result, fieldName, value, isMain);
            }
            // --- 3️⃣ Si es un valor simple ---
            else {
                handleSimpleField(result, fieldName, value, isMain);
            }
        }
        return result;
    }

    private void handleListField(Map<String, Object> result, String fieldName, List<?> list, boolean addLists) {
        if (!addLists || list.isEmpty()) return;

        List<Map<String, Object>> mappedList = new ArrayList<>();
        for (Object item : list) {
            if (item != null) {
                mappedList.add(convertToMap(item, true, false));
            }
        }
        result.put(fieldName, mappedList);
    }

    private void handleComplexField(Map<String, Object> result, String fieldName, Object value, boolean isMain) {
        if (isMain) {
            result.put(fieldName, convertToMap(value, false, false));
        }
    }

    private void handleSimpleField(Map<String, Object> result, String fieldName, Object value, boolean isMain) {
        if (isMain) {
            result.put(fieldName, value);
        } else if (INNER_ALLOWED_FIELDS.stream().anyMatch(f -> f.equalsIgnoreCase(fieldName))) {
            result.put(fieldName, value);
        }
    }

    private List<Field> getAllFields(Class<?> type) {
        List<Field> fields = new ArrayList<>();
        Class<?> current = type;
        while (current != null && current != Object.class) {
            Collections.addAll(fields, current.getDeclaredFields());
            current = current.getSuperclass();
        }
        return fields;
    }

    /**
     * Determina si un objeto es complejo (es decir, no primitivo ni wrapper ni String ni enum)
     */
    private boolean isComplexObject(Object obj) {
        if (obj == null) return false;
        Class<?> clazz = obj.getClass();
        return !(clazz.isPrimitive()
                || clazz.equals(String.class)
                || Number.class.isAssignableFrom(clazz)
                || Boolean.class.isAssignableFrom(clazz)
                || Character.class.isAssignableFrom(clazz)
                || Enum.class.isAssignableFrom(clazz)
                || java.time.temporal.Temporal.class.isAssignableFrom(clazz)
                || UUID.class.isAssignableFrom(clazz)
                || Date.class.isAssignableFrom(clazz));
    }
}
