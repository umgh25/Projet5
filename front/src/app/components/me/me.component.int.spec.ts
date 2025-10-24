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

/**
 * 🧪 Test d'intégration du composant MeComponent
 *
 * Objectif : tester MeComponent branché à ses VRAIS services (SessionService, UserService),
 * avec rendu de template réel (Angular Material) et cycle de vie Angular,
 * tout en MOCKANT uniquement la couche HTTP (backend) via HttpClientTestingModule.
 *
 * On utilise Jest pour les assertions et les mocks.
 */
describe('MeComponent Integration Tests', () => {
  let component: MeComponent;
  let fixture: ComponentFixture<MeComponent>;
  let compiled: HTMLElement;
  let httpMock: HttpTestingController;

  // Services réels injectés par Angular DI
  let sessionService: SessionService;
  let userService: UserService;

  // Doubles de test (mocks Jest) pour Router et MatSnackBar
  let mockRouter: {
    navigate: jest.MockedFunction<(commands: any[]) => Promise<boolean>>;
  };
  let mockMatSnackBar: {
    open: jest.MockedFunction<(message: string, action?: string, config?: any) => any>;
  };

  // ==== Données de test (fixtures) ====

  // Utilisateur admin
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

  // Utilisateur standard
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

  // Informations de session simulées (équivalent du localStorage/session de l'app)
  const mockSessionInfo = {
    token: 'mock-token',
    type: 'Bearer',
    id: 2,
    username: 'testuser',
    firstName: 'John',
    lastName: 'Doe',
    admin: false
  };

  // ==== Configuration du module de test avant chaque test ====
  beforeEach(async () => {
    // Création de mocks Jest pour Router et MatSnackBar uniquement
    mockRouter = {
      navigate: jest.fn().mockResolvedValue(true)
    };
    mockMatSnackBar = {
      open: jest.fn()
    };

    // Configuration du TestBed : on déclare le composant, on importe le module HTTP de test
    // et les modules Angular Material utilisés, et on fournit les SERVICES RÉELS.
    await TestBed.configureTestingModule({
      declarations: [MeComponent],
      imports: [
        HttpClientTestingModule, // Interception et contrôle des requêtes HTTP du UserService
        MatSnackBarModule,
        MatCardModule,
        MatFormFieldModule,
        MatIconModule,
        MatInputModule,
        MatButtonModule
      ],
      providers: [
        SessionService, // vrai service
        UserService,    // vrai service (utilise HttpClient → intercepté par HttpTestingController)
        { provide: Router, useValue: mockRouter },          // mock navigation
        { provide: MatSnackBar, useValue: mockMatSnackBar } // mock snackbar
      ],
    }).compileComponents();

    // Instanciation du composant
    fixture = TestBed.createComponent(MeComponent);
    component = fixture.componentInstance;
    compiled = fixture.nativeElement as HTMLElement;

    // Injection des services réels + contrôleur HTTP
    sessionService = TestBed.inject(SessionService);
    userService = TestBed.inject(UserService);
    httpMock = TestBed.inject(HttpTestingController);

    // Simuler un utilisateur déjà connecté dans la session (avant ngOnInit)
    sessionService.logIn(mockSessionInfo);
  });

  // Vérifie après chaque test qu'aucune requête HTTP n'est restée en suspens
  afterEach(() => {
    httpMock.verify();
  });

  // ==== Tests d'intégration : rendu initial du composant ====
  describe('Initial rendering', () => {
    it('should create component and render basic structure', () => {
      expect(component).toBeTruthy();

      // Déclenche ngOnInit + binding template
      fixture.detectChanges();

      // ✅ Le UserService fait un GET → on l’intercepte et on répond
      const req = httpMock.expectOne('api/user/2');
      expect(req.request.method).toBe('GET');

      // On renvoie un utilisateur standard
      req.flush(mockRegularUser);
      fixture.detectChanges();

      // Vérification que la carte Material est bien rendue
      const matCard = compiled.querySelector('mat-card');
      expect(matCard).toBeTruthy();
    });

    it('should display title "User information"', () => {
      fixture.detectChanges();

      // Interception de la requête de chargement utilisateur
      const req = httpMock.expectOne('api/user/2');
      req.flush(mockRegularUser);
      fixture.detectChanges();

      // Titre attendu
      const title = compiled.querySelector('h1');
      expect(title?.textContent?.trim()).toBe('User information');
    });

    it('should display back button with arrow icon', () => {
      fixture.detectChanges();

      // Interception + réponse
      const req = httpMock.expectOne('api/user/2');
      req.flush(mockRegularUser);
      fixture.detectChanges();

      // Présence du bouton retour
      const backButton = compiled.querySelector('button[mat-icon-button]');
      expect(backButton).toBeTruthy();

      // Icône de flèche attendue
      const arrowIcon = compiled.querySelector('mat-icon');
      expect(arrowIcon?.textContent?.trim()).toBe('arrow_back');
    });
  });

  // ==== Vérification de l’affichage des données utilisateur ====
  describe('User data display', () => {
    beforeEach(() => {
      // On monte le composant et on répond à la requête utilisateur
      fixture.detectChanges();
      const req = httpMock.expectOne('api/user/2');
      req.flush(mockRegularUser);
      fixture.detectChanges();
    });

    it('should display user name with uppercase pipe', () => {
      // Vérifie que le nom/prénom sont rendus avec le lastName en MAJUSCULE
      const nameElement = compiled.querySelector('p');
      expect(nameElement?.textContent).toContain('Name: John DOE');
    });

    it('should display user email', () => {
      // Recherche du paragraphe contenant l’email
      const paragraphs = compiled.querySelectorAll('p');
      const emailParagraph = Array.from(paragraphs).find(p =>
        p.textContent?.includes('Email:')
      );
      expect(emailParagraph?.textContent).toContain('Email: user@yoga.com');
    });

    it('should display creation date with date pipe', () => {
      // Vérifie la présence et le formatage de la date de création (pipe date → texte anglais type "February")
      const paragraphs = compiled.querySelectorAll('p');
      const createDateParagraph = Array.from(paragraphs).find(p =>
        p.textContent?.includes('Create at:')
      );
      expect(createDateParagraph?.textContent).toContain('Create at:');
      expect(createDateParagraph?.textContent).toContain('February');
    });

    it('should display last update date with date pipe', () => {
      // Vérifie la présence et le formatage de la date de mise à jour
      const paragraphs = compiled.querySelectorAll('p');
      const updateDateParagraph = Array.from(paragraphs).find(p =>
        p.textContent?.includes('Last update:')
      );
      expect(updateDateParagraph?.textContent).toContain('Last update:');
      expect(updateDateParagraph?.textContent).toContain('November');
    });
  });

  // ==== Rendu conditionnel selon le rôle (admin vs non-admin) ====
  describe('Conditional rendering based on user role', () => {
    it('should show admin message when user is admin', () => {
      // On remplace la session par un profil admin (id=1)
      sessionService.logIn({ ...mockSessionInfo, id: 1, admin: true });

      // Déclenchement ngOnInit
      fixture.detectChanges();

      // L’URL appelée doit refléter l’id en session
      const req = httpMock.expectOne('api/user/1');
      req.flush(mockAdminUser);
      fixture.detectChanges();

      // Le message "You are admin" doit apparaître
      const adminMessage = compiled.querySelector('p.my2');
      expect(adminMessage?.textContent?.trim()).toBe('You are admin');
    });

    it('should hide admin message when user is not admin', () => {
      fixture.detectChanges();

      // Chargement d’un user non admin
      const req = httpMock.expectOne('api/user/2');
      req.flush(mockRegularUser);
      fixture.detectChanges();

      // Le paragraphe d’info admin ne doit pas exister
      const adminMessage = compiled.querySelector('p.my2');
      expect(adminMessage).toBeFalsy();
    });

    it('should show delete button for non-admin users', () => {
      fixture.detectChanges();

      const req = httpMock.expectOne('api/user/2');
      req.flush(mockRegularUser);
      fixture.detectChanges();

      // Le bouton de suppression doit être visible pour un non-admin
      const deleteButton = compiled.querySelector('button[color="warn"]');
      expect(deleteButton).toBeTruthy();
      expect(deleteButton?.textContent).toContain('Detail'); // texte du bouton dans le template
    });

    it('should hide delete button for admin users', () => {
      // Session admin
      sessionService.logIn({ ...mockSessionInfo, id: 1, admin: true });

      fixture.detectChanges();

      const req = httpMock.expectOne('api/user/1');
      req.flush(mockAdminUser);
      fixture.detectChanges();

      // Pas de bouton delete pour un admin
      const deleteButton = compiled.querySelector('button[color="warn"]');
      expect(deleteButton).toBeFalsy();
    });
  });

  // ==== Interactions utilisateur (clics, effets de bord) ====
  describe('User interactions', () => {
    beforeEach(() => {
      // Chargement initial avec user non admin
      fixture.detectChanges();
      const req = httpMock.expectOne('api/user/2');
      req.flush(mockRegularUser);
      fixture.detectChanges();
    });

    it('should call back() when back button is clicked', () => {
      // On espionne l’API history du navigateur pour confirmer le retour
      const historySpy = jest.spyOn(window.history, 'back').mockImplementation(() => {});

      // Clic sur le bouton retour
      const backButton = compiled.querySelector('button[mat-icon-button]') as HTMLButtonElement;
      backButton.click();

      // Vérifie l’appel
      expect(historySpy).toHaveBeenCalled();

      // Restaure l’implémentation originale
      historySpy.mockRestore();
    });

    it('should call delete() when delete button is clicked', () => {
      // Espion sur la méthode delete du composant
      const deleteSpy = jest.spyOn(component, 'delete');

      // Clic bouton delete
      const deleteButton = compiled.querySelector('button[color="warn"]') as HTMLButtonElement;
      deleteButton.click();

      // La méthode composant doit être appelée
      expect(deleteSpy).toHaveBeenCalled();

      // Et une requête DELETE doit partir côté service
      const req = httpMock.expectOne('api/user/2');
      expect(req.request.method).toBe('DELETE');
      req.flush({});
    });

    it('should trigger real service calls when delete button is clicked', () => {
      // Vérifie l’état session avant suppression
      expect(sessionService.isLogged).toBe(true);

      // Clic bouton delete
      const deleteButton = compiled.querySelector('button[color="warn"]') as HTMLButtonElement;
      deleteButton.click();

      // Attente de la requête DELETE
      const req = httpMock.expectOne('api/user/2');
      expect(req.request.method).toBe('DELETE');

      // Réponse OK
      req.flush({});

      // ✅ Effets de bord attendus : snackbar, logout, navigation
      expect(mockMatSnackBar.open).toHaveBeenCalledWith(
        'Your account has been deleted !',
        'Close',
        { duration: 3000 }
      );
      expect(sessionService.isLogged).toBe(false);
      expect(mockRouter.navigate).toHaveBeenCalledWith(['/']);
    });
  });

  // ==== États de chargement (user non chargé vs chargé) ====
  describe('Loading states', () => {
    it('should not display user content when user is undefined', () => {
      // Forcer un état sans user (avant la résolution HTTP)
      component.user = undefined;

      // Rendu sans data
      fixture.detectChanges();

      // Le bloc principal ne doit pas être présent
      const userContent = compiled.querySelector('div[fxLayout="column"]');
      expect(userContent).toBeFalsy();

      // ⚠️ Important : s’assurer de vider la requête initiale lancée par ngOnInit
      const req = httpMock.match('api/user/2');
      if (req.length > 0) {
        req[0].flush(mockRegularUser);
      }
    });

    it('should display user content when user is loaded', () => {
      // Déclenchement + réponse
      fixture.detectChanges();
      const req = httpMock.expectOne('api/user/2');
      req.flush(mockRegularUser);
      fixture.detectChanges();

      // Le contenu utilisateur est visible
      const userContent = compiled.querySelector('div[fxLayout="column"]');
      expect(userContent).toBeTruthy();
    });

    it('should not display user content initially before ngOnInit', () => {
      // Avant tout detectChanges (donc avant ngOnInit), rien ne doit être rendu
      const userContent = compiled.querySelector('div[fxLayout="column"]');
      expect(userContent).toBeFalsy();
    });
  });

  // ==== Alternative : sélection avec DebugElement ====
  describe('DebugElement approach (Alternative testing method)', () => {
    it('should find elements using DebugElement and CSS selectors', () => {
      fixture.detectChanges();

      // Requête de chargement
      const req = httpMock.expectOne('api/user/2');
      req.flush(mockRegularUser);
      fixture.detectChanges();

      // Sélections via DebugElement (plus précis pour certains cas)
      const backButtonDebug = fixture.debugElement.query(By.css('button[mat-icon-button]'));
      expect(backButtonDebug).toBeTruthy();

      const titleDebug = fixture.debugElement.query(By.css('h1'));
      expect(titleDebug.nativeElement.textContent.trim()).toBe('User information');
    });

    it('should trigger click events using DebugElement', () => {
      fixture.detectChanges();

      // Requête initiale
      const req1 = httpMock.expectOne('api/user/2');
      req1.flush(mockRegularUser);
      fixture.detectChanges();

      const deleteSpy = jest.spyOn(component, 'delete');

      // On cible le bouton via DebugElement
      const deleteButtonDebug = fixture.debugElement.query(By.css('button[color="warn"]'));

      // On déclenche un événement de clic programmatique
      deleteButtonDebug.triggerEventHandler('click', null);

      expect(deleteSpy).toHaveBeenCalled();

      // Attente et validation de la requête DELETE
      const req2 = httpMock.expectOne('api/user/2');
      expect(req2.request.method).toBe('DELETE');
      req2.flush({});
    });
  });
});
