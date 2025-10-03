import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { MatButtonModule } from '@angular/material/button';
import { Router } from '@angular/router';
import { By } from '@angular/platform-browser';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { SessionService } from 'src/app/services/session.service';
import { UserService } from 'src/app/services/user.service';
import { User } from 'src/app/interfaces/user.interface';
import { MatSnackBar } from '@angular/material/snack-bar';
import { expect } from '@jest/globals';

import { MeComponent } from './me.component';

describe('MeComponent Integration Tests', () => {
  let component: MeComponent;
  let fixture: ComponentFixture<MeComponent>;
  let compiled: HTMLElement;
  let httpMock: HttpTestingController;
  
  // Services and mocks
  let sessionService: SessionService;
  let userService: UserService;
  let mockRouter: {
    navigate: jest.MockedFunction<(commands: any[]) => Promise<boolean>>;
  };
  let mockMatSnackBar: {
    open: jest.MockedFunction<(message: string, action?: string, config?: any) => any>;
  };

  // Test data
  const mockAdminUser: User = {
    id: 1,
    email: 'admin@yoga.com',
    firstName: 'Admin',
    lastName: 'SuperUser',
    admin: true,
    password: 'password123',
    createdAt: new Date('2023-01-15'),
    updatedAt: new Date('2023-12-01')
  };

  const mockRegularUser: User = {
    id: 2,
    email: 'user@yoga.com',
    firstName: 'John',
    lastName: 'Doe',
    admin: false,
    password: 'password123',
    createdAt: new Date('2023-02-10'),
    updatedAt: new Date('2023-11-20')
  };

  const mockSessionInfo = {
    token: 'mock-token',
    type: 'Bearer',
    id: 2,
    username: 'testuser',
    firstName: 'John',
    lastName: 'Doe',
    admin: false
  };

  beforeEach(async () => {
    // Create Jest mocks for Router and MatSnackBar only
    mockRouter = {
      navigate: jest.fn().mockResolvedValue(true)
    };
    
    mockMatSnackBar = {
      open: jest.fn()
    };

    await TestBed.configureTestingModule({
      declarations: [MeComponent],
      imports: [
        HttpClientTestingModule, // ✅ For mocking HTTP requests
        MatSnackBarModule,
        MatCardModule,
        MatFormFieldModule,
        MatIconModule,
        MatInputModule,
        MatButtonModule
      ],
      providers: [
        SessionService, // ✅ REAL service
        UserService,    // ✅ REAL service
        { provide: Router, useValue: mockRouter },
        { provide: MatSnackBar, useValue: mockMatSnackBar }
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(MeComponent);
    component = fixture.componentInstance;
    compiled = fixture.nativeElement as HTMLElement;
    
    // Get REAL services
    sessionService = TestBed.inject(SessionService);
    userService = TestBed.inject(UserService);
    httpMock = TestBed.inject(HttpTestingController);
    
    // Simulate logged-in user session
    sessionService.logIn(mockSessionInfo);
  });

  afterEach(() => {
    // Verify no outstanding HTTP requests
    httpMock.verify();
  });

  describe('Initial rendering', () => {
    it('should create component and render basic structure', () => {
      expect(component).toBeTruthy();
      
      // Trigger ngOnInit which calls the real service
      fixture.detectChanges();
      
      // Intercept the REAL HTTP call made by UserService
      const req = httpMock.expectOne('api/user/2');
      expect(req.request.method).toBe('GET');
      
      // Respond with mock data
      req.flush(mockRegularUser);
      fixture.detectChanges();
      
      // Check if mat-card is rendered
      const matCard = compiled.querySelector('mat-card');
      expect(matCard).toBeTruthy();
    });

    it('should display title "User information"', () => {
      fixture.detectChanges();
      
      // Handle the HTTP request
      const req = httpMock.expectOne('api/user/2');
      req.flush(mockRegularUser);
      fixture.detectChanges();
      
      const title = compiled.querySelector('h1');
      expect(title?.textContent?.trim()).toBe('User information');
    });

    it('should display back button with arrow icon', () => {
      fixture.detectChanges();
      
      // Handle the HTTP request
      const req = httpMock.expectOne('api/user/2');
      req.flush(mockRegularUser);
      fixture.detectChanges();
      
      const backButton = compiled.querySelector('button[mat-icon-button]');
      expect(backButton).toBeTruthy();
      
      const arrowIcon = compiled.querySelector('mat-icon');
      expect(arrowIcon?.textContent?.trim()).toBe('arrow_back');
    });
  });

  describe('User data display', () => {
    beforeEach(() => {
      // Trigger ngOnInit and handle HTTP request
      fixture.detectChanges();
      const req = httpMock.expectOne('api/user/2');
      req.flush(mockRegularUser);
      fixture.detectChanges();
    });

    it('should display user name with uppercase pipe', () => {
      const nameElement = compiled.querySelector('p');
      expect(nameElement?.textContent).toContain('Name: John DOE'); // lastName should be uppercase
    });

    it('should display user email', () => {
      const paragraphs = compiled.querySelectorAll('p');
      const emailParagraph = Array.from(paragraphs).find(p => 
        p.textContent?.includes('Email:')
      );
      expect(emailParagraph?.textContent).toContain('Email: user@yoga.com');
    });

    it('should display creation date with date pipe', () => {
      const paragraphs = compiled.querySelectorAll('p');
      const createDateParagraph = Array.from(paragraphs).find(p => 
        p.textContent?.includes('Create at:')
      );
      expect(createDateParagraph?.textContent).toContain('Create at:');
      expect(createDateParagraph?.textContent).toContain('February'); // Should format date
    });

    it('should display last update date with date pipe', () => {
      const paragraphs = compiled.querySelectorAll('p');
      const updateDateParagraph = Array.from(paragraphs).find(p => 
        p.textContent?.includes('Last update:')
      );
      expect(updateDateParagraph?.textContent).toContain('Last update:');
      expect(updateDateParagraph?.textContent).toContain('November'); // Should format date
    });
  });

  describe('Conditional rendering based on user role', () => {
    it('should show admin message when user is admin', () => {
      // Simulate admin user session
      sessionService.logIn({ ...mockSessionInfo, id: 1, admin: true });
      
      // Trigger ngOnInit
      fixture.detectChanges();
      
      // Intercept and respond with admin user data
      const req = httpMock.expectOne('api/user/1');
      req.flush(mockAdminUser);
      fixture.detectChanges();
      
      // Assert - Admin message should be visible
      const adminMessage = compiled.querySelector('p.my2');
      expect(adminMessage?.textContent?.trim()).toBe('You are admin');
    });

    it('should hide admin message when user is not admin', () => {
      // Regular user session already set in beforeEach
      fixture.detectChanges();
      
      // Handle HTTP request
      const req = httpMock.expectOne('api/user/2');
      req.flush(mockRegularUser);
      fixture.detectChanges();
      
      const adminMessage = compiled.querySelector('p.my2');
      expect(adminMessage).toBeFalsy(); // Should not exist for regular users
    });

    it('should show delete button for non-admin users', () => {
      fixture.detectChanges();
      
      // Handle HTTP request
      const req = httpMock.expectOne('api/user/2');
      req.flush(mockRegularUser);
      fixture.detectChanges();
      
      const deleteButton = compiled.querySelector('button[color="warn"]');
      expect(deleteButton).toBeTruthy();
      expect(deleteButton?.textContent).toContain('Detail'); // Button text
    });

    it('should hide delete button for admin users', () => {
      // Simulate admin user session
      sessionService.logIn({ ...mockSessionInfo, id: 1, admin: true });
      
      // Trigger ngOnInit
      fixture.detectChanges();
      
      // Intercept and respond with admin user data
      const req = httpMock.expectOne('api/user/1');
      req.flush(mockAdminUser);
      fixture.detectChanges();
      
      // Assert - Delete button should not exist
      const deleteButton = compiled.querySelector('button[color="warn"]');
      expect(deleteButton).toBeFalsy();
    });
  });

  describe('User interactions', () => {
    beforeEach(() => {
      // Initialize component and handle HTTP request
      fixture.detectChanges();
      const req = httpMock.expectOne('api/user/2');
      req.flush(mockRegularUser);
      fixture.detectChanges();
    });

    it('should call back() when back button is clicked', () => {
      // Arrange - Spy on window.history.back
      const historySpy = jest.spyOn(window.history, 'back').mockImplementation(() => {});
      
      // Act - Click the back button
      const backButton = compiled.querySelector('button[mat-icon-button]') as HTMLButtonElement;
      backButton.click();
      
      // Assert
      expect(historySpy).toHaveBeenCalled();
      
      // Cleanup
      historySpy.mockRestore();
    });

    it('should call delete() when delete button is clicked', () => {
      // Arrange - Spy on component delete method
      const deleteSpy = jest.spyOn(component, 'delete');
      
      // Act - Click the delete button
      const deleteButton = compiled.querySelector('button[color="warn"]') as HTMLButtonElement;
      deleteButton.click();
      
      // Assert
      expect(deleteSpy).toHaveBeenCalled();
      
      // Handle the DELETE HTTP request triggered by delete()
      const req = httpMock.expectOne('api/user/2');
      expect(req.request.method).toBe('DELETE');
      req.flush({});
    });

    it('should trigger real service calls when delete button is clicked', () => {
      // Verify initial session state
      expect(sessionService.isLogged).toBe(true);
      
      // Act - Click delete button
      const deleteButton = compiled.querySelector('button[color="warn"]') as HTMLButtonElement;
      deleteButton.click();
      
      // Intercept the REAL HTTP DELETE request made by UserService
      const req = httpMock.expectOne('api/user/2');
      expect(req.request.method).toBe('DELETE');
      
      // Respond to the request
      req.flush({});
      
      // Assert - Verify side effects
      expect(mockMatSnackBar.open).toHaveBeenCalledWith(
        'Your account has been deleted !', 
        'Close', 
        { duration: 3000 }
      );
      expect(sessionService.isLogged).toBe(false);
      expect(mockRouter.navigate).toHaveBeenCalledWith(['/']);
    });
  });

  describe('Loading states', () => {
    it('should not display user content when user is undefined', () => {
      // Manually set user to undefined (component exists but no data loaded yet)
      component.user = undefined;
      
      // Render without user data
      fixture.detectChanges();
      
      // Assert - User content should not be visible
      const userContent = compiled.querySelector('div[fxLayout="column"]');
      expect(userContent).toBeFalsy();
      
      // Note: ngOnInit was already called in global beforeEach
      // We need to flush that request to avoid test errors
      const req = httpMock.match('api/user/2');
      if (req.length > 0) {
        req[0].flush(mockRegularUser);
      }
    });

    it('should display user content when user is loaded', () => {
      // Trigger ngOnInit and load user data
      fixture.detectChanges();
      
      const req = httpMock.expectOne('api/user/2');
      req.flush(mockRegularUser);
      fixture.detectChanges();
      
      const userContent = compiled.querySelector('div[fxLayout="column"]');
      expect(userContent).toBeTruthy();
    });

    it('should not display user content initially before ngOnInit', () => {
      // Create component but do NOT call fixture.detectChanges()
      // Don't call fixture.detectChanges() yet
      
      const userContent = compiled.querySelector('div[fxLayout="column"]');
      expect(userContent).toBeFalsy();
    });
  });

  describe('DebugElement approach (Alternative testing method)', () => {
    it('should find elements using DebugElement and CSS selectors', () => {
      fixture.detectChanges();
      
      // Handle HTTP request
      const req = httpMock.expectOne('api/user/2');
      req.flush(mockRegularUser);
      fixture.detectChanges();
      
      // Using DebugElement for more precise element selection
      const backButtonDebug = fixture.debugElement.query(By.css('button[mat-icon-button]'));
      expect(backButtonDebug).toBeTruthy();
      
      const titleDebug = fixture.debugElement.query(By.css('h1'));
      expect(titleDebug.nativeElement.textContent.trim()).toBe('User information');
    });

    it('should trigger click events using DebugElement', () => {
      fixture.detectChanges();
      
      // Handle initial HTTP request
      const req1 = httpMock.expectOne('api/user/2');
      req1.flush(mockRegularUser);
      fixture.detectChanges();
      
      const deleteSpy = jest.spyOn(component, 'delete');
      
      // Find delete button using DebugElement
      const deleteButtonDebug = fixture.debugElement.query(By.css('button[color="warn"]'));
      
      // Trigger click event
      deleteButtonDebug.triggerEventHandler('click', null);
      
      expect(deleteSpy).toHaveBeenCalled();
      
      // Handle DELETE HTTP request
      const req2 = httpMock.expectOne('api/user/2');
      expect(req2.request.method).toBe('DELETE');
      req2.flush({});
    });
  });
});