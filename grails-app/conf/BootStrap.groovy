/*
*   Copyright 2012-2013 The Regents of the University of Colorado
*
*   Licensed under the Apache License, Version 2.0 (the "License")
*   you may not use this file except in compliance with the License.
*   You may obtain a copy of the License at
*
*       http://www.apache.org/licenses/LICENSE-2.0
*
*   Unless required by applicable law or agreed to in writing, software
*   distributed under the License is distributed on an "AS IS" BASIS,
*   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
*   See the License for the specific language governing permissions and
*   limitations under the License.
*/

import com.recomdata.grails.domain.Permission;
import com.recomdata.grails.domain.Person;
import com.recomdata.grails.domain.Requestmap;
import com.recomdata.grails.domain.Role;
import com.recomdata.grails.domain.User;
import com.recomdata.grails.domain.UserGroup;
import com.recomdata.grails.domain.WorkflowTitle;

class BootStrap {
	
	def dataSource;
	def authenticateService;

    def init = { servletContext ->
		
		log.info("Bootstrapping...")
		
		def test=Role.findByAuthority("ROLE_ADMIN");
		if(test==null){
			println("Creating seed data for authentication")
			def aRole = Role.findByAuthority("ROLE_ADMIN");
			if(aRole ==null){
				aRole = new Role(authority:"ROLE_ADMIN", description:"Admin User", displayName:"System Admin", securityLevel:1);
				aRole.save();
			}

			def uRole = Role.findByAuthority("ROLE_USER");
			if(uRole ==null){
				uRole = new Role(authority:"ROLE_USER", description:"User", displayName:"Standard User", securityLevel:2);
				uRole.save(flush:true);
			}
			
			def viewperm = Permission.findByName("VIEW");
			if(viewperm==null){
				viewperm = new Permission(name:"VIEW",description:"VIEW PERMISSION", uid:"PERM:VIEW");
				viewperm = viewperm.save(flush:true);
			}
			
			/**
			* Make a group called "Administrators"
			*/
		   def g = UserGroup.findByName("Administrators");
		   if(g==null) {
			   g=new UserGroup(name:"Administrators", description:"System administrators group").save();
		   }

		   /**
			* Make an admin person
			*/
		   def sadmin = Person.findByUsername("rosita")
		   if(sadmin==null) {
			   sadmin= new Person();
			   sadmin.username="rosita";
			   sadmin.password =authenticateService.encodePassword("password");
			   sadmin.email="sadmin@recomdata.com"
			   sadmin.save(flush:true)
		   }

		   /*
			* put admin in role admin
			*/
		   sadmin.addToAuthorities(aRole)
		   aRole.addToPeople(sadmin)
		   aRole.save(flush:true)
		   //aRole.errors.each{println(it)}
		   /**
			* put admin in rs admin group
			*/
		   def u1=User.findByUsername("rosita");
		   /**
			* Add admin user to administrators group and save group
			*/
		   g.addToMembers(u1) //add to admin
		   g.save(flush:true)
		   
		}
		
		test = Requestmap.list();
		if (!test) {
			//Request map
			new Requestmap(configAttribute: "IS_AUTHENTICATED_REMEMBERED", url: "/**").save()
			new Requestmap(configAttribute: "IS_AUTHENTICATED_ANONYMOUSLY", url: "/css/**").save()
			new Requestmap(configAttribute: "IS_AUTHENTICATED_ANONYMOUSLY", url: "/images/**").save()
			new Requestmap(configAttribute: "IS_AUTHENTICATED_ANONYMOUSLY", url: "/js/**").save()
			new Requestmap(configAttribute: "IS_AUTHENTICATED_ANONYMOUSLY", url: "/login/**").save()
			new Requestmap(configAttribute: "IS_AUTHENTICATED_ANONYMOUSLY", url: "/plugins/*/images/**").save()
			new Requestmap(configAttribute: "IS_AUTHENTICATED_ANONYMOUSLY", url: "/plugins/*/css/**").save()
			new Requestmap(configAttribute: "IS_AUTHENTICATED_ANONYMOUSLY", url: "/plugins/*/js/**").save(flush: true)
		}
		
		test = WorkflowTitle.list();
		if (!test) {
			//Workflow titles
			new WorkflowTitle(id:1, title: "Verify Files").save()
			new WorkflowTitle(id:2, title: "Validate Source Files").save()
			new WorkflowTitle(id:3, title: "Load Source Tables").save()
			new WorkflowTitle(id:4, title: "Profile Source Tables").save()
			new WorkflowTitle(id:5, title: "Validate Source Tables").save()
			new WorkflowTitle(id:6, title: "Export Unmapped Source Values").save()
			new WorkflowTitle(id:7, title: "Import Vocabulary Mappings").save()
			new WorkflowTitle(id:8, title: "Process to OMOP").save()
			new WorkflowTitle(id:9, title: "Profile OMOP Tables").save()
			new WorkflowTitle(id:10, title: "Validate OMOP Tables").save()
			new WorkflowTitle(id:11, title: "Publish to GRID").save()
			new WorkflowTitle(id:12, title: "Backup Persistent Data").save()
			new WorkflowTitle(id:13, title: "Complete").save(flush: true)
		}
    }
    def destroy = {
    }
}
