import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FeatureSales } from './feature-sales';

describe('FeatureSales', () => {
  let component: FeatureSales;
  let fixture: ComponentFixture<FeatureSales>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [FeatureSales],
    }).compileComponents();

    fixture = TestBed.createComponent(FeatureSales);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
