/*
 * Copyright 2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.springlets.security.service.api;

import java.util.List;

import io.springlets.security.domain.UserLoginRole;

/**
 * 
 * = API of the service related with the entity {@link UserLoginRole}
 *
 * Interface that defines the operations related with the entity {@link UserLoginRole}.
 * 
 * @author Cèsar Ordiñana at http://www.disid.com[DISID Corporation S.L.]
 * @author Juan Carlos García at http://www.disid.com[DISID Corporation S.L.]
 */
public interface UserLoginRoleService {

  //=== CRUD Methods

  UserLoginRole save(UserLoginRole userLoginRole);

  void delete(Long id);

  // === Batch CRUD Methods

  List<UserLoginRole> save(Iterable<UserLoginRole> userLoginRoles);

  void delete(Iterable<Long> ids);

  // === Finders

  List<UserLoginRole> findAll();

  List<UserLoginRole> findAll(Iterable<Long> ids);

  UserLoginRole findOne(Long id);

}
