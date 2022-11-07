package kg.peaksoft.taskTrackerb6.enums;

import org.springframework.security.core.GrantedAuthority;

public enum Role implements GrantedAuthority {

   SUPER_ADMIN,
   ADMIN,
   USER;

   @Override
   public String getAuthority() {
      return name();
   }
}
